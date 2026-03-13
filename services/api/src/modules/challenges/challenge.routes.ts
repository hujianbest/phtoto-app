import type { FastifyInstance } from "fastify";
import { challengeService } from "./challenge.service";

interface JoinChallengeBody {
  email?: unknown;
}

interface ChallengeStatusQuery {
  email?: unknown;
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function validateEmail(value: unknown): string | null {
  if (typeof value !== "string") {
    return null;
  }
  const email = value.trim().toLowerCase();
  if (!EMAIL_REGEX.test(email)) {
    return null;
  }
  return email;
}

export function registerChallengeRoutes(app: FastifyInstance) {
  app.post<{ Body: JoinChallengeBody }>("/challenges/weekly/join", async (req, reply) => {
    const email = validateEmail(req.body?.email);
    if (!email) {
      return reply.code(400).send({ message: "email is required and must be valid." });
    }

    const result = challengeService.joinWeeklyChallenge(email);
    return reply.code(200).send(result);
  });

  app.get<{ Querystring: ChallengeStatusQuery }>("/challenges/weekly/status", async (req, reply) => {
    const email = validateEmail(req.query?.email);
    if (!email) {
      return reply.code(400).send({ message: "email is required and must be valid." });
    }

    const result = challengeService.getWeeklyStatus(email);
    return reply.code(200).send(result);
  });
}
