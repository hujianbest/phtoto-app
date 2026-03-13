export interface PostExif {
  aperture?: number;
  iso?: number;
  shutter?: string;
}

export interface Post {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  intent: string;
  authorEmail: string;
  exif: PostExif;
  createdAt: string;
}

export interface CreatePostInput {
  title: string;
  description: string;
  imageUrl: string;
  intent: string;
  authorEmail?: string;
  exif?: unknown;
}
