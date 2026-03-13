import { randomUUID } from "crypto";
import type { CreateReviewInput, Review, StructuredReview } from "./review.entity";

const STRUCTURED_KEYS = ["composition", "light", "color", "story", "postprocess"] as const;

type StructuredKey = (typeof STRUCTURED_KEYS)[number];

function normalizeComment(input: unknown): string | undefined {
  if (typeof input !== "string") {
    return undefined;
  }

  const value = input.trim();
  if (value.length === 0) {
    return undefined;
  }
  return value;
}

function normalizeStructured(input: unknown): StructuredReview | undefined {
  if (!input || typeof input !== "object" || Array.isArray(input)) {
    return undefined;
  }

  const source = input as Record<string, unknown>;
  const normalized: StructuredReview = {};

  for (const key of STRUCTURED_KEYS) {
    const value = source[key];
    if (typeof value === "number" && Number.isFinite(value) && value >= 1 && value <= 5) {
      normalized[key as StructuredKey] = value;
    }
  }

  if (Object.keys(normalized).length === 0) {
    return undefined;
  }

  return normalized;
}

export class ReviewService {
  private readonly reviews = new Map<string, Review>();
  private readonly helpfulByReview = new Map<string, Set<string>>();

  create(input: CreateReviewInput): Review {
    const comment = normalizeComment(input.comment);
    const structured = normalizeStructured(input.structured);
    if (!comment && !structured) {
      throw new Error("Review requires comment or structured feedback.");
    }

    const review: Review = {
      id: randomUUID(),
      postId: input.postId,
      comment,
      structured,
      helpfulCount: 0
    };

    this.reviews.set(review.id, review);
    return review;
  }

  markHelpful(id: string, voterKey: string): Review | undefined {
    const review = this.reviews.get(id);
    if (!review) {
      return undefined;
    }

    const normalizedVoterKey = voterKey.trim().toLowerCase();
    if (!normalizedVoterKey) {
      return review;
    }

    const voters = this.helpfulByReview.get(id) ?? new Set<string>();
    if (voters.has(normalizedVoterKey)) {
      return review;
    }

    voters.add(normalizedVoterKey);
    this.helpfulByReview.set(id, voters);
    review.helpfulCount += 1;
    return review;
  }

  listByPostId(postId: string): Review[] {
    return [...this.reviews.values()].filter((review) => review.postId === postId);
  }
}

export const reviewService = new ReviewService();
