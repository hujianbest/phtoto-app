import request = require("supertest");
import { buildServer } from "../src/server";

describe("health route", () => {
  it("GET /health returns ok", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const res = await request(app.server).get("/health");

      expect(res.status).toBe(200);
      expect(res.body.status).toBe("ok");
    } finally {
      await app.close();
    }
  });
});
