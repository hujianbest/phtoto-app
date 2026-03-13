import type { FastifyInstance } from "fastify";
import { postService } from "../posts/post.service";
import { reviewService } from "./review.service";

interface CreateReviewBody {
  comment?: unknown;
  structured?: unknown;
}

interface CreateReviewParams {
  id: string;
}

interface MarkHelpfulParams {
  id: string;
}

export class ReviewError extends Error {
  constructor(
    public readonly statusCode: number,
    message: string
  ) {
    super(message);
  }
}

function validateCreateReviewBody(body?: CreateReviewBody): void {
  if (!body) {
    return;
  }

  if (body.comment !== undefined && typeof body.comment !== "string") {
    throw new ReviewError(400, "comment must be a string.");
  }

  if (
    body.structured !== undefined &&
    (typeof body.structured !== "object" || body.structured === null || Array.isArray(body.structured))
  ) {
    throw new ReviewError(400, "structured must be an object.");
  }
}

export function registerReviewRoutes(app: FastifyInstance) {
  app.post<{ Params: CreateReviewParams; Body: CreateReviewBody }>("/posts/:id/reviews", async (req, reply) => {
    try {
      validateCreateReviewBody(req.body);
      if (!postService.getById(req.params.id)) {
        return reply.code(404).send({ message: "Post not found." });
      }
      const created = reviewService.create({
        postId: req.params.id,
        comment: req.body?.comment,
        structured: req.body?.structured
      });
      return reply.code(201).send(created);
    } catch (error) {
      if (error instanceof Error && error.message === "Review requires comment or structured feedback.") {
        return reply.code(400).send({ message: error.message });
      }
      if (error instanceof ReviewError) {
        return reply.code(error.statusCode).send({ message: error.message });
      }
      throw error;
    }
  });

  app.post<{ Params: MarkHelpfulParams }>("/reviews/:id/helpful", async (req, reply) => {
    const headerClientId = req.headers["x-client-id"];
    const voterKey = (Array.isArray(headerClientId) ? headerClientId[0] : headerClientId) ?? req.ip ?? "anonymous";
    const updated = reviewService.markHelpful(req.params.id, voterKey);
    if (!updated) {
      return reply.code(404).send({ message: "Review not found." });
    }
    return reply.code(200).send(updated);
  });
}
