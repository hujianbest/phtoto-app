import request = require("supertest");
import { buildServer } from "../src/server";

describe("weekly challenge routes", () => {
  it("POST /challenges/weekly/join is idempotent for same email", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const first = await request(app.server).post("/challenges/weekly/join").send({
        email: "joiner@example.com"
      });
      const second = await request(app.server).post("/challenges/weekly/join").send({
        email: "joiner@example.com"
      });

      expect(first.status).toBe(200);
      expect(second.status).toBe(200);
      expect(typeof first.body.joinedAt).toBe("string");
      expect(second.body.joinedAt).toBe(first.body.joinedAt);
    } finally {
      await app.close();
    }
  });

  it("GET /challenges/weekly/status returns joined status", async () => {
    const app = buildServer();
    try {
      await app.ready();
      await request(app.server).post("/challenges/weekly/join").send({
        email: "status@example.com"
      });

      const joined = await request(app.server).get("/challenges/weekly/status").query({
        email: "status@example.com"
      });
      const unjoined = await request(app.server).get("/challenges/weekly/status").query({
        email: "new@example.com"
      });

      expect(joined.status).toBe(200);
      expect(joined.body.joined).toBe(true);
      expect(typeof joined.body.joinedAt).toBe("string");

      expect(unjoined.status).toBe(200);
      expect(unjoined.body.joined).toBe(false);
      expect(unjoined.body.joinedAt).toBeNull();
    } finally {
      await app.close();
    }
  });
});
