import request = require("supertest");
import { buildServer } from "../src/server";

describe("moderation and report flow", () => {
  it("high-risk content should be blocked", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const highRiskRes = await request(app.server).post("/moderation/check").send({
        text: "我想组织暴力袭击并教人做炸弹"
      });
      expect(highRiskRes.status).toBe(200);
      expect(highRiskRes.body.riskLevel).toBe("high");
      expect(highRiskRes.body.blocked).toBe(true);
      expect(highRiskRes.body.decision).toBe("blocked");

      const mediumRiskRes = await request(app.server).post("/moderation/check").send({
        text: "这内容有点像广告引流，可能是spam"
      });
      expect(mediumRiskRes.status).toBe(200);
      expect(mediumRiskRes.body.riskLevel).toBe("medium");
      expect(mediumRiskRes.body.blocked).toBe(false);
      expect(mediumRiskRes.body.decision).toBe("review");

      const lowRiskRes = await request(app.server).post("/moderation/check").send({
        text: "今天分享一张城市夜景照片"
      });
      expect(lowRiskRes.status).toBe(200);
      expect(lowRiskRes.body.riskLevel).toBe("low");
      expect(lowRiskRes.body.blocked).toBe(false);
      expect(lowRiskRes.body.decision).toBe("pass");

      const boundaryRes = await request(app.server).post("/moderation/check").send({
        text: "I improved my skill in composition today."
      });
      expect(boundaryRes.status).toBe(200);
      expect(boundaryRes.body.riskLevel).toBe("low");

      const tooLongModerationText = await request(app.server).post("/moderation/check").send({
        text: "a".repeat(2001)
      });
      expect(tooLongModerationText.status).toBe(400);

      const maxModerationText = await request(app.server).post("/moderation/check").send({
        text: "a".repeat(2000)
      });
      expect(maxModerationText.status).toBe(200);
    } finally {
      await app.close();
    }
  });

  it("report create + status transition flow", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const created = await request(app.server).post("/reports").send({
        targetType: "post",
        targetId: "post-123",
        reason: "suspected plagiarism"
      });
      expect(created.status).toBe(201);
      expect(typeof created.body.id).toBe("string");
      expect(created.body.targetType).toBe("post");
      expect(created.body.targetId).toBe("post-123");
      expect(created.body.reason).toBe("suspected plagiarism");
      expect(created.body.status).toBe("pending");

      const reviewed = await request(app.server).patch(`/reports/${created.body.id}/status`).send({
        status: "reviewed"
      });
      expect(reviewed.status).toBe(200);
      expect(reviewed.body.status).toBe("reviewed");

      const closed = await request(app.server).patch(`/reports/${created.body.id}/status`).send({
        status: "closed"
      });
      expect(closed.status).toBe(200);
      expect(closed.body.status).toBe("closed");

      const invalidAfterClosed = await request(app.server).patch(`/reports/${created.body.id}/status`).send({
        status: "reviewed"
      });
      expect(invalidAfterClosed.status).toBe(409);

      const pendingToClosedDirect = await request(app.server).post("/reports").send({
        targetType: "review",
        targetId: "review-888",
        reason: "abuse"
      });
      expect(pendingToClosedDirect.status).toBe(201);

      const invalidSkip = await request(app.server)
        .patch(`/reports/${pendingToClosedDirect.body.id}/status`)
        .send({ status: "closed" });
      expect(invalidSkip.status).toBe(409);

      const tooLongReason = await request(app.server).post("/reports").send({
        targetType: "user",
        targetId: "user-1",
        reason: "x".repeat(501)
      });
      expect(tooLongReason.status).toBe(400);

      const maxReason = await request(app.server).post("/reports").send({
        targetType: "user",
        targetId: "user-2",
        reason: "x".repeat(500)
      });
      expect(maxReason.status).toBe(201);

      const tooLongTargetId = await request(app.server).post("/reports").send({
        targetType: "user",
        targetId: "u".repeat(129),
        reason: "too long target id"
      });
      expect(tooLongTargetId.status).toBe(400);

      const maxTargetId = await request(app.server).post("/reports").send({
        targetType: "user",
        targetId: "u".repeat(128),
        reason: "boundary accepted"
      });
      expect(maxTargetId.status).toBe(201);
    } finally {
      await app.close();
    }
  });
});
