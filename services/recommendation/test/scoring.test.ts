import { buildRecommendationSignals, rankRecommendedPosts } from "../src/scoring";

describe("recommendation scoring", () => {
  it("higher quality score ranks first", () => {
    const ranked = rankRecommendedPosts([
      { id: "post-low", qualityScore: 0.2, reviewScore: 0.5, freshnessScore: 0.5 },
      { id: "post-high", qualityScore: 0.9, reviewScore: 0.5, freshnessScore: 0.5 }
    ]);

    expect(ranked.map((item) => item.id)).toEqual(["post-high", "post-low"]);
  });

  it("calculates total score using configured weights", () => {
    const ranked = rankRecommendedPosts([{ id: "post-a", qualityScore: 1, reviewScore: 0.5, freshnessScore: 0 }]);

    expect(ranked[0].totalScore).toBeCloseTo(0.65, 6);
    expect(ranked[0].weightVersion).toBe("v1");
  });

  it("builds signals where newer post gets higher freshness", () => {
    const now = Date.parse("2026-03-13T00:00:00.000Z");
    const signals = buildRecommendationSignals(
      [
        {
          id: "old-post",
          title: "Old title",
          description: "Old description content",
          intent: "Old intent text",
          exifFieldCount: 2,
          createdAt: "2026-02-27T00:00:00.000Z"
        },
        {
          id: "new-post",
          title: "New title",
          description: "New description content",
          intent: "New intent text",
          exifFieldCount: 2,
          createdAt: "2026-03-13T00:00:00.000Z"
        }
      ],
      {
        "old-post": [{ helpfulCount: 0, structuredValues: [3, 3, 3] }],
        "new-post": [{ helpfulCount: 0, structuredValues: [3, 3, 3] }]
      },
      now
    );

    const oldPost = signals.find((item) => item.id === "old-post");
    const newPost = signals.find((item) => item.id === "new-post");
    expect(oldPost).toBeDefined();
    expect(newPost).toBeDefined();
    expect(newPost!.freshnessScore).toBeGreaterThan(oldPost!.freshnessScore);
  });
});
