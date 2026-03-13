import request = require("supertest");
import { buildServer } from "../src/server";

describe("metrics sli endpoint", () => {
  it("GET /metrics returns text with core SLI names", async () => {
    const app = buildServer();

    try {
      await app.ready();
      const res = await request(app.server).get("/metrics");

      expect(res.status).toBe(200);
      expect(res.headers["content-type"]).toContain("text/plain");
      expect(res.text).toContain("availability");
      expect(res.text).toContain("latency");
      expect(res.text).toContain("error_rate");
    } finally {
      await app.close();
    }
  });

  it("GET /metrics follows basic prometheus exposition format", async () => {
    const app = buildServer();

    try {
      await app.ready();
      const res = await request(app.server).get("/metrics");
      const lines = res.text.trim().split("\n");

      expect(lines).toContain("# HELP availability Service availability ratio in [0,1]");
      expect(lines).toContain("# TYPE availability gauge");
      expect(lines).toContain("# HELP latency Request latency in milliseconds");
      expect(lines).toContain("# TYPE latency gauge");
      expect(lines).toContain("# HELP error_rate Request error ratio in [0,1]");
      expect(lines).toContain("# TYPE error_rate gauge");
      expect(lines.some((line) => /^availability\s+\d+(\.\d+)?$/.test(line))).toBe(true);
      expect(lines.some((line) => /^latency\s+\d+(\.\d+)?$/.test(line))).toBe(true);
      expect(lines.some((line) => /^error_rate\s+\d+(\.\d+)?$/.test(line))).toBe(true);
    } finally {
      await app.close();
    }
  });

  it("GET /metrics exposes values in expected ranges", async () => {
    const app = buildServer();
    try {
      await app.ready();

      await request(app.server).get("/health");
      const res = await request(app.server).get("/metrics");
      const lines = res.text.trim().split("\n");

      const availabilityLine = lines.find((line) => line.startsWith("availability "));
      const latencyLine = lines.find((line) => line.startsWith("latency "));
      const errorRateLine = lines.find((line) => line.startsWith("error_rate "));

      expect(availabilityLine).toBeDefined();
      expect(latencyLine).toBeDefined();
      expect(errorRateLine).toBeDefined();

      const availability = Number((availabilityLine ?? "").split(" ")[1]);
      const latency = Number((latencyLine ?? "").split(" ")[1]);
      const errorRate = Number((errorRateLine ?? "").split(" ")[1]);

      expect(availability).toBeGreaterThanOrEqual(0);
      expect(availability).toBeLessThanOrEqual(1);
      expect(errorRate).toBeGreaterThanOrEqual(0);
      expect(errorRate).toBeLessThanOrEqual(1);
      expect(latency).toBeGreaterThanOrEqual(0);
    } finally {
      await app.close();
    }
  });
});
