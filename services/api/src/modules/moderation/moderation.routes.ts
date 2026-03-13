import type { FastifyInstance } from "fastify";
import { moderationService } from "./moderation.service";

interface ModerationBody {
  text?: unknown;
}

export function registerModerationRoutes(app: FastifyInstance) {
  app.post<{ Body: ModerationBody }>("/moderation/check", async (req, reply) => {
    const text = req.body?.text;
    if (typeof text !== "string" || text.trim().length === 0) {
      return reply.code(400).send({ message: "text is required." });
    }
    if (text.length > 2000) {
      return reply.code(400).send({ message: "text must be at most 2000 characters." });
    }

    const result = moderationService.checkTextContent(text);
    return reply.code(200).send(result);
  });
}
