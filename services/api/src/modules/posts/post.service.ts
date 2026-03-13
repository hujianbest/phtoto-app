import { randomUUID } from "crypto";
import { extractExif } from "./exif.extractor";
import type { CreatePostInput, Post } from "./post.entity";

export class PostService {
  private readonly posts = new Map<string, Post>();

  create(input: CreatePostInput): Post {
    const post: Post = {
      id: randomUUID(),
      title: input.title.trim(),
      description: input.description.trim(),
      imageUrl: input.imageUrl.trim(),
      intent: input.intent.trim(),
      authorEmail: input.authorEmail?.trim().toLowerCase() || "anonymous@photo.app",
      exif: extractExif(input.exif),
      metadata: {
        genre: input.metadata?.genre?.trim(),
        gearBrand: input.metadata?.gearBrand?.trim().toLowerCase(),
        city: input.metadata?.city?.trim().toLowerCase(),
        challengeTag: input.metadata?.challengeTag?.trim().toLowerCase()
      },
      createdAt: new Date().toISOString()
    };

    this.posts.set(post.id, post);
    return post;
  }

  getById(id: string): Post | undefined {
    return this.posts.get(id);
  }

  list(): Post[] {
    return [...this.posts.values()];
  }
}

export const postService = new PostService();
