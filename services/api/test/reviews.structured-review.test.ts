import request = require("supertest");
import { buildServer } from "../src/server";

describe("reviews routes", () => {
  it("POST /posts/:id/reviews creates structured review", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const createPostRes = await request(app.server).post("/posts").send({
        title: "City lights",
        description: "Night photo",
        imageUrl: "https://cdn.example.com/city.jpg",
        intent: "Capture mood"
      });
      expect(createPostRes.status).toBe(201);

      const res = await request(app.server).post(`/posts/${createPostRes.body.id}/reviews`).send({
        comment: "Nice control of highlights",
        structured: {
          composition: 4,
          light: 5,
          color: 4,
          story: 3,
          postprocess: 4
        }
      });

      expect(res.status).toBe(201);
      expect(typeof res.body.id).toBe("string");
      expect(res.body.postId).toBe(createPostRes.body.id);
      expect(res.body.comment).toBe("Nice control of highlights");
      expect(res.body.structured).toEqual({
        composition: 4,
        light: 5,
        color: 4,
        story: 3,
        postprocess: 4
      });
      expect(res.body.helpfulCount).toBe(0);
    } finally {
      await app.close();
    }
  });

  it("POST /reviews/:id/helpful increments helpfulCount", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const createPostRes = await request(app.server).post("/posts").send({
        title: "Forest walk",
        description: "Morning fog",
        imageUrl: "https://cdn.example.com/forest.jpg",
        intent: "Calm and soft"
      });
      expect(createPostRes.status).toBe(201);

      const createReviewRes = await request(app.server).post(`/posts/${createPostRes.body.id}/reviews`).send({
        comment: "Can improve framing"
      });
      expect(createReviewRes.status).toBe(201);
      expect(createReviewRes.body.helpfulCount).toBe(0);

      const helpfulRes = await request(app.server)
        .post(`/reviews/${createReviewRes.body.id}/helpful`)
        .set("x-client-id", "client-a")
        .send();
      expect(helpfulRes.status).toBe(200);
      expect(helpfulRes.body.id).toBe(createReviewRes.body.id);
      expect(helpfulRes.body.helpfulCount).toBe(1);

      const duplicateHelpfulRes = await request(app.server)
        .post(`/reviews/${createReviewRes.body.id}/helpful`)
        .set("x-client-id", "client-a")
        .send();
      expect(duplicateHelpfulRes.status).toBe(200);
      expect(duplicateHelpfulRes.body.helpfulCount).toBe(1);

      const anotherClientRes = await request(app.server)
        .post(`/reviews/${createReviewRes.body.id}/helpful`)
        .set("x-client-id", "client-b")
        .send();
      expect(anotherClientRes.status).toBe(200);
      expect(anotherClientRes.body.helpfulCount).toBe(2);
    } finally {
      await app.close();
    }
  });

  it("POST /posts/:id/reviews returns 404 when post does not exist", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const res = await request(app.server).post("/posts/non-existing-post/reviews").send({
        comment: "Cannot attach to missing post"
      });

      expect(res.status).toBe(404);
    } finally {
      await app.close();
    }
  });

  it("POST /posts/:id/reviews returns 400 for empty feedback payload", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const createPostRes = await request(app.server).post("/posts").send({
        title: "Studio portrait",
        description: "Controlled light",
        imageUrl: "https://cdn.example.com/studio.jpg",
        intent: "Evaluate skin tones"
      });
      expect(createPostRes.status).toBe(201);

      const emptyReview = await request(app.server).post(`/posts/${createPostRes.body.id}/reviews`).send({});
      expect(emptyReview.status).toBe(400);

      const invalidStructuredOnly = await request(app.server).post(`/posts/${createPostRes.body.id}/reviews`).send({
        structured: {
          composition: 9,
          light: -1
        }
      });
      expect(invalidStructuredOnly.status).toBe(400);
    } finally {
      await app.close();
    }
  });
});
