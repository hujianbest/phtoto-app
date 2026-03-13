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
      exif: extractExif(input.exif),
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
