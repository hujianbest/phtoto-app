export interface StructuredReview {
  composition?: number;
  light?: number;
  color?: number;
  story?: number;
  postprocess?: number;
}

export interface Review {
  id: string;
  postId: string;
  comment?: string;
  structured?: StructuredReview;
  helpfulCount: number;
}

export interface CreateReviewInput {
  postId: string;
  comment?: unknown;
  structured?: unknown;
}
