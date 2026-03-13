export interface PostExif {
  aperture?: number;
  iso?: number;
  shutter?: string;
}

export interface PostMetadata {
  genre?: string;
  gearBrand?: string;
  city?: string;
  challengeTag?: string;
}

export interface Post {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  intent: string;
  authorEmail: string;
  exif: PostExif;
  metadata: PostMetadata;
  createdAt: string;
}

export interface CreatePostInput {
  title: string;
  description: string;
  imageUrl: string;
  intent: string;
  authorEmail?: string;
  metadata?: PostMetadata;
  exif?: unknown;
}
