import type { FastifyInstance } from "fastify";
import { postService } from "./post.service";

interface PostBody {
  title?: string;
  description?: string;
  imageUrl?: string;
  intent?: string;
  exif?: unknown;
}

export class PostError extends Error {
  constructor(
    public readonly statusCode: number,
    message: string
  ) {
    super(message);
  }
}

function validatePostBody(
  body?: PostBody
): asserts body is { title: string; description: string; imageUrl: string; intent: string; exif?: unknown } {
  if (!body) {
    throw new PostError(400, "Request body is required.");
  }

  const requiredFields: Array<keyof Omit<PostBody, "exif">> = ["title", "description", "imageUrl", "intent"];
  for (const field of requiredFields) {
    const value = body[field];
    if (typeof value !== "string" || value.trim().length === 0) {
      throw new PostError(400, `${field} is required.`);
    }
  }

  const imageUrlValue = body.imageUrl;
  if (typeof imageUrlValue !== "string") {
    throw new PostError(400, "imageUrl is required.");
  }

  const imageUrl = imageUrlValue.trim();
  let parsedUrl: URL;
  try {
    parsedUrl = new URL(imageUrl);
  } catch {
    throw new PostError(400, "imageUrl must be a valid http/https URL.");
  }

  if (parsedUrl.protocol !== "http:" && parsedUrl.protocol !== "https:") {
    throw new PostError(400, "imageUrl must be a valid http/https URL.");
  }
}

export function registerPostRoutes(app: FastifyInstance) {
  app.post<{ Body: PostBody }>("/posts", async (req, reply) => {
    try {
      validatePostBody(req.body);
      const created = postService.create(req.body);
      return reply.code(201).send(created);
    } catch (error) {
      if (error instanceof PostError) {
        return reply.code(error.statusCode).send({ message: error.message });
      }
      throw error;
    }
  });
}
