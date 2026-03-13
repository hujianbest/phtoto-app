export interface RecommendationSignals {
  id: string;
  qualityScore: number;
  reviewScore: number;
  freshnessScore: number;
}

export interface RecommendationPostInput {
  id: string;
  title: string;
  description: string;
  intent: string;
  exifFieldCount: number;
  createdAt: string;
}

export interface RecommendationReviewInput {
  helpfulCount: number;
  structuredValues?: number[];
}

export interface RankedRecommendation extends RecommendationSignals {
  totalScore: number;
  weightVersion: string;
}

const WEIGHTS = {
  quality: 0.5,
  review: 0.3,
  freshness: 0.2
} as const;
const WEIGHT_VERSION = "v1";
const FRESHNESS_HALF_LIFE_MS = 7 * 24 * 60 * 60 * 1000;

function clamp(value: number): number {
  if (value < 0) {
    return 0;
  }
  if (value > 1) {
    return 1;
  }
  return value;
}

function calculateQualityScore(post: RecommendationPostInput): number {
  const textScore = post.title.trim().length >= 8 && post.description.trim().length >= 12 ? 0.4 : 0.2;
  const intentScore = post.intent.trim().length >= 8 ? 0.3 : 0.15;
  const exifScore = clamp(post.exifFieldCount / 3);
  return clamp(textScore + intentScore + exifScore * 0.3);
}

function calculateReviewScore(reviews: RecommendationReviewInput[]): number {
  if (reviews.length === 0) {
    return 0;
  }

  const total = reviews.reduce((sum, review) => {
    const structured = review.structuredValues?.length
      ? review.structuredValues.reduce((acc, item) => acc + item, 0) / (review.structuredValues.length * 5)
      : 0;
    const helpful = clamp(review.helpfulCount * 0.1);
    return sum + clamp(structured * 0.8 + helpful * 0.2);
  }, 0);

  return clamp(total / reviews.length);
}

function calculateFreshnessScore(createdAt: string, nowMs: number): number {
  const ageMs = Math.max(0, nowMs - Date.parse(createdAt));
  return clamp(Math.pow(0.5, ageMs / FRESHNESS_HALF_LIFE_MS));
}

function scoreOne(item: RecommendationSignals): RankedRecommendation {
  const totalScore =
    item.qualityScore * WEIGHTS.quality +
    item.reviewScore * WEIGHTS.review +
    item.freshnessScore * WEIGHTS.freshness;

  return {
    ...item,
    totalScore,
    weightVersion: WEIGHT_VERSION
  };
}

export function buildRecommendationSignals(
  posts: RecommendationPostInput[],
  reviewsByPostId: Record<string, RecommendationReviewInput[]>,
  nowMs = Date.now()
): RecommendationSignals[] {
  return posts.map((post) => ({
    id: post.id,
    qualityScore: calculateQualityScore(post),
    reviewScore: calculateReviewScore(reviewsByPostId[post.id] ?? []),
    freshnessScore: calculateFreshnessScore(post.createdAt, nowMs)
  }));
}

export function rankRecommendedPosts(items: RecommendationSignals[]): RankedRecommendation[] {
  return items
    .map(scoreOne)
    .sort((a, b) => b.totalScore - a.totalScore);
}
