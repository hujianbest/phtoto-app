import request = require("supertest");
import { buildServer } from "../src/server";

describe("follow feed routes", () => {
  it("GET /feed/following returns only followed authors posts", async () => {
    const app = buildServer();
    try {
      await app.ready();

      const followedPost = await request(app.server).post("/posts").send({
        title: "Followed author work",
        description: "post by followed author",
        imageUrl: "https://cdn.example.com/followed.jpg",
        intent: "follow-feed test",
        authorEmail: "alice@example.com"
      });
      const unfollowedPost = await request(app.server).post("/posts").send({
        title: "Unfollowed author work",
        description: "post by unfollowed author",
        imageUrl: "https://cdn.example.com/unfollowed.jpg",
        intent: "follow-feed test",
        authorEmail: "bob@example.com"
      });
      expect(followedPost.status).toBe(201);
      expect(unfollowedPost.status).toBe(201);

      const followRes = await request(app.server).post("/social/follow").send({
        followerEmail: "viewer@example.com",
        followeeEmail: "alice@example.com"
      });
      expect(followRes.status).toBe(200);

      const feedRes = await request(app.server).get("/feed/following").query({
        email: "viewer@example.com"
      });
      expect(feedRes.status).toBe(200);
      expect(Array.isArray(feedRes.body.items)).toBe(true);

      const ids = feedRes.body.items.map((item: { id: string }) => item.id);
      expect(ids).toContain(followedPost.body.id);
      expect(ids).not.toContain(unfollowedPost.body.id);
    } finally {
      await app.close();
    }
  });
});
