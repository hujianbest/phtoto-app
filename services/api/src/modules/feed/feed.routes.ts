import type { FastifyInstance } from "fastify";
import { buildRecommendationSignals, rankRecommendedPosts } from "recommendation";
import { postService } from "../posts/post.service";
import { reviewService } from "../reviews/review.service";

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
  app.get("/feed/recommended", async (_req, reply) => {
    const posts = postService.list();
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
}
