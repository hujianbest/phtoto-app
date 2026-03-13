import type { FastifyInstance } from "fastify";
import { followService } from "./follow.service";

interface FollowBody {
  followerEmail?: unknown;
  followeeEmail?: unknown;
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function normalizeEmail(value: unknown): string | null {
  if (typeof value !== "string") {
    return null;
  }
  const email = value.trim().toLowerCase();
  if (!EMAIL_REGEX.test(email)) {
    return null;
  }
  return email;
}

export function registerFollowRoutes(app: FastifyInstance) {
  app.post<{ Body: FollowBody }>("/social/follow", async (req, reply) => {
    const followerEmail = normalizeEmail(req.body?.followerEmail);
    const followeeEmail = normalizeEmail(req.body?.followeeEmail);
    if (!followerEmail || !followeeEmail) {
      return reply.code(400).send({ message: "followerEmail and followeeEmail must be valid emails." });
    }
    if (followerEmail === followeeEmail) {
      return reply.code(400).send({ message: "cannot follow self." });
    }

    const result = followService.follow(followerEmail, followeeEmail);
    return reply.code(200).send(result);
  });
}
