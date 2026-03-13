import request = require("supertest");
import jwt, { type JwtPayload } from "jsonwebtoken";
import { buildServer } from "../src/server";

function decodePayload(token: string): Record<string, unknown> {
  const payload = token.split(".")[1];
  if (!payload) {
    throw new Error("Invalid JWT token format.");
  }

  const json = Buffer.from(payload, "base64url").toString("utf8");
  return JSON.parse(json) as Record<string, unknown>;
}

describe("auth routes", () => {
  const jwtSecret = "task3-test-secret";
  const jwtIssuer = "photo-app-api";
  const jwtAudience = "photo-app-clients";

  beforeEach(() => {
    process.env.JWT_SECRET = jwtSecret;
  });

  it("POST /auth/register returns token with userId", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const res = await request(app.server).post("/auth/register").send({
        email: "alice@example.com",
        password: "pa55w0rd"
      });

      expect(res.status).toBe(201);
      expect(typeof res.body.token).toBe("string");

      const payload = decodePayload(res.body.token);
      expect(typeof payload.userId).toBe("string");

      const verified = jwt.verify(res.body.token, jwtSecret, {
        issuer: jwtIssuer,
        audience: jwtAudience
      }) as JwtPayload;
      expect(typeof verified.exp).toBe("number");
    } finally {
      await app.close();
    }
  });

  it("POST /auth/login returns token with same userId", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const registerRes = await request(app.server).post("/auth/register").send({
        email: "bob@example.com",
        password: "pa55w0rd"
      });
      const loginRes = await request(app.server).post("/auth/login").send({
        email: "bob@example.com",
        password: "pa55w0rd"
      });

      expect(registerRes.status).toBe(201);
      expect(loginRes.status).toBe(200);

      const registerPayload = decodePayload(registerRes.body.token);
      const loginPayload = decodePayload(loginRes.body.token);
      expect(loginPayload.userId).toBe(registerPayload.userId);
    } finally {
      await app.close();
    }
  });

  it("POST /auth/register returns 409 for duplicate email", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const payload = {
        email: "duplicate@example.com",
        password: "pa55w0rd"
      };

      const first = await request(app.server).post("/auth/register").send(payload);
      const second = await request(app.server).post("/auth/register").send(payload);

      expect(first.status).toBe(201);
      expect(second.status).toBe(409);
    } finally {
      await app.close();
    }
  });

  it("POST /auth/login returns 401 for wrong password", async () => {
    const app = buildServer();
    try {
      await app.ready();
      await request(app.server).post("/auth/register").send({
        email: "wrong-password@example.com",
        password: "pa55w0rd"
      });

      const res = await request(app.server).post("/auth/login").send({
        email: "wrong-password@example.com",
        password: "wrong-pass-123"
      });

      expect(res.status).toBe(401);
    } finally {
      await app.close();
    }
  });

  it("returns 400 for missing or empty fields", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const missing = await request(app.server).post("/auth/register").send({
        email: "missing-pass@example.com"
      });
      const empty = await request(app.server).post("/auth/register").send({
        email: "   ",
        password: ""
      });
      const invalidEmail = await request(app.server).post("/auth/login").send({
        email: "invalid-email",
        password: "pa55w0rd"
      });
      const shortPassword = await request(app.server).post("/auth/login").send({
        email: "short-pass@example.com",
        password: "1234567"
      });

      expect(missing.status).toBe(400);
      expect(empty.status).toBe(400);
      expect(invalidEmail.status).toBe(400);
      expect(shortPassword.status).toBe(400);
    } finally {
      await app.close();
    }
  });
});
