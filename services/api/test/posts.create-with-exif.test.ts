import request = require("supertest");
import { buildServer } from "../src/server";
import { postService } from "../src/modules/posts/post.service";

describe("posts routes", () => {
  it("POST /posts creates post and stores normalized exif fields", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const res = await request(app.server).post("/posts").send({
        title: "Night street",
        description: "Shot after rain",
        imageUrl: "https://cdn.example.com/night.jpg",
        intent: "Capture reflections and mood",
        exif: {
          aperture: "2.8",
          iso: "400",
          shutter: "1/125"
        }
      });

      expect(res.status).toBe(201);
      expect(typeof res.body.id).toBe("string");
      expect(res.body.title).toBe("Night street");
      expect(res.body.description).toBe("Shot after rain");
      expect(res.body.imageUrl).toBe("https://cdn.example.com/night.jpg");
      expect(res.body.intent).toBe("Capture reflections and mood");
      expect(res.body.exif).toEqual({
        aperture: 2.8,
        iso: 400,
        shutter: "1/125"
      });

      const stored = postService.getById(res.body.id);
      expect(stored).toBeDefined();
      expect(stored?.exif).toEqual({
        aperture: 2.8,
        iso: 400,
        shutter: "1/125"
      });
    } finally {
      await app.close();
    }
  });

  it("POST /posts returns 400 for missing or empty required fields", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const missing = await request(app.server).post("/posts").send({
        title: "A title",
        description: "A description",
        imageUrl: "https://cdn.example.com/img.jpg"
      });
      const empty = await request(app.server).post("/posts").send({
        title: "   ",
        description: "",
        imageUrl: "https://cdn.example.com/img.jpg",
        intent: "   "
      });

      expect(missing.status).toBe(400);
      expect(empty.status).toBe(400);
    } finally {
      await app.close();
    }
  });

  it("POST /posts returns 400 for non-http imageUrl", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const invalidSchema = await request(app.server).post("/posts").send({
        title: "Bad URL",
        description: "Wrong schema",
        imageUrl: "ftp://cdn.example.com/img.jpg",
        intent: "test"
      });
      const invalidFormat = await request(app.server).post("/posts").send({
        title: "Bad URL",
        description: "Wrong format",
        imageUrl: "not-a-url",
        intent: "test"
      });

      expect(invalidSchema.status).toBe(400);
      expect(invalidFormat.status).toBe(400);
    } finally {
      await app.close();
    }
  });

  it("POST /posts ignores invalid exif numeric fields", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const res = await request(app.server).post("/posts").send({
        title: "Strict exif",
        description: "Do not truncate",
        imageUrl: "https://cdn.example.com/strict.jpg",
        intent: "test strict parsing",
        exif: {
          aperture: "2.8x",
          iso: "400abc",
          shutter: "abc"
        }
      });

      expect(res.status).toBe(201);
      expect(res.body.exif).toEqual({});
    } finally {
      await app.close();
    }
  });

  it("POST /posts only accepts valid shutter formats", async () => {
    const app = buildServer();
    try {
      await app.ready();
      const invalid = await request(app.server).post("/posts").send({
        title: "Invalid shutter",
        description: "invalid shutter variants",
        imageUrl: "https://cdn.example.com/invalid-shutter.jpg",
        intent: "validate shutter parser",
        exif: {
          shutter: "1/0"
        }
      });

      const invalidLeadingZero = await request(app.server).post("/posts").send({
        title: "Invalid shutter",
        description: "leading zero fraction",
        imageUrl: "https://cdn.example.com/invalid-shutter-2.jpg",
        intent: "validate shutter parser",
        exif: {
          shutter: "01/125"
        }
      });

      const validFraction = await request(app.server).post("/posts").send({
        title: "Valid shutter",
        description: "fraction shutter",
        imageUrl: "https://cdn.example.com/valid-shutter.jpg",
        intent: "validate shutter parser",
        exif: {
          shutter: "1/125"
        }
      });

      const validDecimal = await request(app.server).post("/posts").send({
        title: "Valid shutter",
        description: "decimal shutter",
        imageUrl: "https://cdn.example.com/valid-shutter-2.jpg",
        intent: "validate shutter parser",
        exif: {
          shutter: "0.008"
        }
      });

      const validNumber = await request(app.server).post("/posts").send({
        title: "Valid shutter",
        description: "number shutter",
        imageUrl: "https://cdn.example.com/valid-shutter-3.jpg",
        intent: "validate shutter parser",
        exif: {
          shutter: 0.016
        }
      });

      expect(invalid.status).toBe(201);
      expect(invalidLeadingZero.status).toBe(201);
      expect(validFraction.status).toBe(201);
      expect(validDecimal.status).toBe(201);
      expect(validNumber.status).toBe(201);

      expect(invalid.body.exif).toEqual({});
      expect(invalidLeadingZero.body.exif).toEqual({});
      expect(validFraction.body.exif).toEqual({ shutter: "1/125" });
      expect(validDecimal.body.exif).toEqual({ shutter: "0.008" });
      expect(validNumber.body.exif).toEqual({ shutter: "0.016" });

      expect(postService.getById(invalid.body.id)?.exif).toEqual({});
      expect(postService.getById(invalidLeadingZero.body.id)?.exif).toEqual({});
      expect(postService.getById(validFraction.body.id)?.exif).toEqual({ shutter: "1/125" });
      expect(postService.getById(validDecimal.body.id)?.exif).toEqual({ shutter: "0.008" });
      expect(postService.getById(validNumber.body.id)?.exif).toEqual({ shutter: "0.016" });
    } finally {
      await app.close();
    }
  });
});
