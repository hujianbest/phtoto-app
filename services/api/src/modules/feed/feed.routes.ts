import type { FastifyInstance } from "fastify";
import { buildRecommendationSignals, rankRecommendedPosts } from "recommendation";
import { postService } from "../posts/post.service";
import { reviewService } from "../reviews/review.service";
import { followService } from "../social/follow.service";

function clamp(value: number): number {
  if (value < 0) {
    return 0;
  }
  if (value > 1) {
    return 1;
  }
  return value;
}

export function registerFeedRoutes(app: FastifyInstance) {
  app.get<{
    Querystring: {
      keyword?: string;
      genre?: string;
      gearBrand?: string;
      city?: string;
      challengeTag?: string;
    };
  }>("/feed/recommended", async (req, reply) => {
    const keyword = req.query.keyword?.trim().toLowerCase() ?? "";
    const genre = req.query.genre?.trim().toLowerCase() ?? "";
    const gearBrand = req.query.gearBrand?.trim().toLowerCase() ?? "";
    const city = req.query.city?.trim().toLowerCase() ?? "";
    const challengeTag = req.query.challengeTag?.trim().toLowerCase() ?? "";
    const allPosts = postService.list();
    const posts = allPosts.filter((post) => {
      const keywordMatched =
        keyword.length === 0 ||
        post.title.toLowerCase().includes(keyword) ||
        post.description.toLowerCase().includes(keyword) ||
        post.intent.toLowerCase().includes(keyword);
      const genreMatched = genre.length === 0 || (post.metadata.genre ?? "").toLowerCase() === genre;
      const gearBrandMatched = gearBrand.length === 0 || (post.metadata.gearBrand ?? "").toLowerCase() === gearBrand;
      const cityMatched = city.length === 0 || (post.metadata.city ?? "").toLowerCase() === city;
      const challengeMatched =
        challengeTag.length === 0 || (post.metadata.challengeTag ?? "").toLowerCase() === challengeTag;
      return keywordMatched && genreMatched && gearBrandMatched && cityMatched && challengeMatched;
    });
    const reviewsByPostId = Object.fromEntries(
      posts.map((post) => [
        post.id,
        reviewService.listByPostId(post.id).map((review) => ({
          helpfulCount: review.helpfulCount,
          structuredValues: review.structured ? Object.values(review.structured) : []
        }))
      ])
    );
    const signals = buildRecommendationSignals(
      posts.map((post) => ({
        id: post.id,
        title: post.title,
        description: post.description,
        intent: post.intent,
        exifFieldCount: Object.keys(post.exif).length,
        createdAt: post.createdAt
      })),
      reviewsByPostId
    );
    const ranked = rankRecommendedPosts(
      signals.map((item) => ({
        ...item,
        qualityScore: clamp(item.qualityScore),
        reviewScore: clamp(item.reviewScore),
        freshnessScore: clamp(item.freshnessScore)
      }))
    );

    const postById = new Map(posts.map((post) => [post.id, post]));
    return reply.code(200).send({
      items: ranked.map((item) => ({
        ...postById.get(item.id),
        qualityScore: item.qualityScore,
        reviewScore: item.reviewScore,
        freshnessScore: item.freshnessScore,
        totalScore: item.totalScore,
        weightVersion: item.weightVersion
      }))
    });
  });

  app.get<{ Querystring: { email?: string } }>("/feed/following", async (req, reply) => {
    const email = req.query.email?.trim().toLowerCase();
    if (!email) {
      return reply.code(400).send({ message: "email is required." });
    }

    const following = followService.getFollowing(email);
    const posts = postService
      .list()
      .filter((post) => following.has(post.authorEmail))
      .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1));

    return reply.code(200).send({ items: posts });
  });
}
