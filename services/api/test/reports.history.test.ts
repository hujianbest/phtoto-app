import request = require("supertest");
import { buildServer } from "../src/server";

describe("report history routes", () => {
  it("GET /reports returns history filtered by reporterEmail", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const reportA = await request(app.server).post("/reports").send({
        targetType: "post",
        targetId: "post-a",
        reason: "spam content",
        reporterEmail: "alice@example.com"
      });
      const reportB = await request(app.server).post("/reports").send({
        targetType: "post",
        targetId: "post-b",
        reason: "abusive words",
        reporterEmail: "bob@example.com"
      });
      expect(reportA.status).toBe(201);
      expect(reportB.status).toBe(201);

      const res = await request(app.server).get("/reports").query({
        reporterEmail: "alice@example.com"
      });
      expect(res.status).toBe(200);
      expect(Array.isArray(res.body.items)).toBe(true);
      expect(res.body.items.length).toBeGreaterThanOrEqual(1);
      expect(res.body.items.every((item: { reporterEmail: string }) => item.reporterEmail === "alice@example.com")).toBe(
        true
      );
    } finally {
      await app.close();
    }
  });
});
