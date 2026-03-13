import { randomUUID } from "crypto";

export type ReportTargetType = "post" | "review" | "user";
export type ReportStatus = "pending" | "reviewed" | "closed";

export interface Report {
  id: string;
  targetType: ReportTargetType;
  targetId: string;
  reason: string;
  reporterEmail: string;
  status: ReportStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateReportInput {
  targetType: ReportTargetType;
  targetId: string;
  reason: string;
  reporterEmail?: string;
}

export class ReportStore {
  private readonly reports = new Map<string, Report>();

  create(input: CreateReportInput): Report {
    const now = new Date().toISOString();
    const report: Report = {
      id: randomUUID(),
      targetType: input.targetType,
      targetId: input.targetId.trim(),
      reason: input.reason.trim(),
      reporterEmail: input.reporterEmail?.trim().toLowerCase() || "anonymous@photo.app",
      status: "pending",
      createdAt: now,
      updatedAt: now
    };
    this.reports.set(report.id, report);
    return report;
  }

  updateStatus(id: string, status: Exclude<ReportStatus, "pending">): Report | undefined {
    const report = this.reports.get(id);
    if (!report) {
      return undefined;
    }

    if (report.status === "pending" && status === "closed") {
      throw new Error("Invalid transition: pending must move to reviewed first.");
    }

    if (report.status === "closed") {
      throw new Error("Invalid transition: closed report cannot be changed.");
    }

    if (report.status === status) {
      return report;
    }

    report.status = status;
    report.updatedAt = new Date().toISOString();
    return report;
  }

  listByReporterEmail(reporterEmail: string): Report[] {
    const normalized = reporterEmail.trim().toLowerCase();
    return [...this.reports.values()]
      .filter((report) => report.reporterEmail === normalized)
      .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1));
  }
}

export const reportStore = new ReportStore();
