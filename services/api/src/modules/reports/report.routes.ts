import type { FastifyInstance } from "fastify";
import { reportStore, type ReportTargetType } from "./report.entity";

interface CreateReportBody {
  targetType?: unknown;
  targetId?: unknown;
  reason?: unknown;
  reporterEmail?: unknown;
}

interface ReportStatusParams {
  id: string;
}

interface UpdateReportStatusBody {
  status?: unknown;
}

interface ReportHistoryQuery {
  reporterEmail?: unknown;
}

const VALID_TARGET_TYPES: readonly ReportTargetType[] = ["post", "review", "user"];
const VALID_TRANSITION_STATUSES = ["reviewed", "closed"] as const;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function isValidTargetType(value: unknown): value is ReportTargetType {
  return typeof value === "string" && VALID_TARGET_TYPES.includes(value as ReportTargetType);
}

function isValidTransitionStatus(value: unknown): value is (typeof VALID_TRANSITION_STATUSES)[number] {
  return typeof value === "string" && VALID_TRANSITION_STATUSES.includes(value as "reviewed" | "closed");
}

export function registerReportRoutes(app: FastifyInstance) {
  app.get<{ Querystring: ReportHistoryQuery }>("/reports", async (req, reply) => {
    const reporterEmail = req.query?.reporterEmail;
    if (typeof reporterEmail !== "string" || !EMAIL_REGEX.test(reporterEmail.trim().toLowerCase())) {
      return reply.code(400).send({ message: "reporterEmail is required and must be valid." });
    }

    const items = reportStore.listByReporterEmail(reporterEmail);
    return reply.code(200).send({ items });
  });

  app.post<{ Body: CreateReportBody }>("/reports", async (req, reply) => {
    const { targetType, targetId, reason, reporterEmail } = req.body ?? {};
    if (!isValidTargetType(targetType)) {
      return reply.code(400).send({ message: "targetType must be one of post/review/user." });
    }
    if (typeof targetId !== "string" || targetId.trim().length === 0) {
      return reply.code(400).send({ message: "targetId is required." });
    }
    if (targetId.length > 128) {
      return reply.code(400).send({ message: "targetId must be at most 128 characters." });
    }
    if (typeof reason !== "string" || reason.trim().length === 0) {
      return reply.code(400).send({ message: "reason is required." });
    }
    if (reason.length > 500) {
      return reply.code(400).send({ message: "reason must be at most 500 characters." });
    }
    if (reporterEmail !== undefined) {
      if (typeof reporterEmail !== "string" || !EMAIL_REGEX.test(reporterEmail.trim().toLowerCase())) {
        return reply.code(400).send({ message: "reporterEmail must be a valid email." });
      }
    }

    const created = reportStore.create({
      targetType,
      targetId,
      reason,
      reporterEmail: typeof reporterEmail === "string" ? reporterEmail : undefined
    });
    return reply.code(201).send(created);
  });

  app.patch<{ Params: ReportStatusParams; Body: UpdateReportStatusBody }>("/reports/:id/status", async (req, reply) => {
    const nextStatus = req.body?.status;
    if (!isValidTransitionStatus(nextStatus)) {
      return reply.code(400).send({ message: "status must be reviewed or closed." });
    }

    try {
      const updated = reportStore.updateStatus(req.params.id, nextStatus);
      if (!updated) {
        return reply.code(404).send({ message: "Report not found." });
      }
      return reply.code(200).send(updated);
    } catch (error) {
      if (error instanceof Error) {
        return reply.code(409).send({ message: error.message });
      }
      throw error;
    }
  });
}
