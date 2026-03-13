import request = require("supertest");
import { buildServer } from "../src/server";
import { postService } from "../src/modules/posts/post.service";

describe("feed recommendation route", () => {
  it("GET /feed/recommended returns posts sorted by recommendation score", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const highQuality = await request(app.server).post("/posts").send({
        title: "Sharp mountain",
        description: "Layered light and depth",
        imageUrl: "https://cdn.example.com/mountain.jpg",
        intent: "Show foreground separation",
        exif: {
          aperture: "2.8",
          iso: "100",
          shutter: "1/250"
        }
      });
      expect(highQuality.status).toBe(201);

      const lowQuality = await request(app.server).post("/posts").send({
        title: "Quick snap",
        description: "basic",
        imageUrl: "https://cdn.example.com/snap.jpg",
        intent: "test"
      });
      expect(lowQuality.status).toBe(201);

      const review = await request(app.server).post(`/posts/${highQuality.body.id}/reviews`).send({
        structured: {
          composition: 5,
          light: 4,
          color: 4,
          story: 4,
          postprocess: 4
        }
      });
      expect(review.status).toBe(201);

      const helpful = await request(app.server)
        .post(`/reviews/${review.body.id}/helpful`)
        .set("x-client-id", "reviewer-a")
        .send();
      expect(helpful.status).toBe(200);

      const res = await request(app.server).get("/feed/recommended");
      expect(res.status).toBe(200);
      expect(Array.isArray(res.body.items)).toBe(true);

      const ids = res.body.items.map((item: { id: string }) => item.id);
      expect(ids[0]).toBe(highQuality.body.id);
      expect(ids[1]).toBe(lowQuality.body.id);
      expect(res.body.items[0].weightVersion).toBe("v1");
      expect(typeof res.body.items[0].totalScore).toBe("number");
      expect(res.body.items[0].freshnessScore).toBeGreaterThanOrEqual(0);
      expect(res.body.items[0].freshnessScore).toBeLessThanOrEqual(1);
    } finally {
      await app.close();
    }
  });

  it("GET /feed/recommended gives newer posts higher freshness", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const olderRes = await request(app.server).post("/posts").send({
        title: "Older scene",
        description: "older reference post",
        imageUrl: "https://cdn.example.com/older.jpg",
        intent: "freshness check"
      });
      expect(olderRes.status).toBe(201);

      const newerRes = await request(app.server).post("/posts").send({
        title: "Newer scene",
        description: "newer reference post",
        imageUrl: "https://cdn.example.com/newer.jpg",
        intent: "freshness check"
      });
      expect(newerRes.status).toBe(201);

      const older = postService.getById(olderRes.body.id);
      const newer = postService.getById(newerRes.body.id);
      expect(older).toBeDefined();
      expect(newer).toBeDefined();
      if (older && newer) {
        older.createdAt = new Date(Date.now() - 14 * 24 * 60 * 60 * 1000).toISOString();
        newer.createdAt = new Date().toISOString();
      }

      const feedRes = await request(app.server).get("/feed/recommended");
      expect(feedRes.status).toBe(200);

      const olderItem = feedRes.body.items.find((item: { id: string }) => item.id === olderRes.body.id);
      const newerItem = feedRes.body.items.find((item: { id: string }) => item.id === newerRes.body.id);
      expect(olderItem).toBeDefined();
      expect(newerItem).toBeDefined();
      expect(newerItem.freshnessScore).toBeGreaterThan(olderItem.freshnessScore);
    } finally {
      await app.close();
    }
  });
});
