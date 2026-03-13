import { randomUUID } from "crypto";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import type { User } from "../users/user.entity";

interface AuthInput {
  email: string;
  password: string;
}

export class AuthService {
  private readonly usersByEmail = new Map<string, User>();
  private readonly jwtIssuer = "photo-app-api";
  private readonly jwtAudience = "photo-app-clients";
  private readonly jwtExpiresIn = "1h";

  async register(input: AuthInput): Promise<{ token: string }> {
    const email = input.email.trim().toLowerCase();
    if (this.usersByEmail.has(email)) {
      throw new AuthError(409, "Email already exists.");
    }

    const passwordHash = await bcrypt.hash(input.password, 10);
    const user: User = {
      id: randomUUID(),
      email,
      passwordHash
    };
    this.usersByEmail.set(email, user);

    return { token: this.createToken(user.id) };
  }

  async login(input: AuthInput): Promise<{ token: string }> {
    const email = input.email.trim().toLowerCase();
    const user = this.usersByEmail.get(email);
    if (!user) {
      throw new AuthError(401, "Invalid email or password.");
    }

    const isValidPassword = await bcrypt.compare(input.password, user.passwordHash);
    if (!isValidPassword) {
      throw new AuthError(401, "Invalid email or password.");
    }

    return { token: this.createToken(user.id) };
  }

  private createToken(userId: string): string {
    const jwtSecret = process.env.JWT_SECRET;
    if (!jwtSecret || jwtSecret.trim().length === 0) {
      throw new AuthError(500, "JWT secret is not configured.");
    }

    return jwt.sign({ userId }, jwtSecret, {
      expiresIn: this.jwtExpiresIn,
      issuer: this.jwtIssuer,
      audience: this.jwtAudience
    });
  }
}

export class AuthError extends Error {
  constructor(
    public readonly statusCode: number,
    message: string
  ) {
    super(message);
  }
}

export const authService = new AuthService();
