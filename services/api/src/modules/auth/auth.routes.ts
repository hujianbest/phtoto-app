import type { FastifyInstance } from "fastify";
import { AuthError, authService } from "./auth.service";

interface AuthBody {
  email?: string;
  password?: string;
}

function validateAuthBody(body?: AuthBody): asserts body is { email: string; password: string } {
  if (!body || typeof body.email !== "string" || typeof body.password !== "string") {
    throw new AuthError(400, "Email and password are required.");
  }

  const email = body.email.trim();
  const password = body.password.trim();
  const isEmailFormatValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  if (!email || !password) {
    throw new AuthError(400, "Email and password cannot be empty.");
  }

  if (!isEmailFormatValid) {
    throw new AuthError(400, "Email format is invalid.");
  }

  if (password.length < 8) {
    throw new AuthError(400, "Password must be at least 8 characters.");
  }
}

export function registerAuthRoutes(app: FastifyInstance) {
  app.post<{ Body: AuthBody }>("/auth/register", async (req, reply) => {
    try {
      validateAuthBody(req.body);
      const result = await authService.register(req.body);
      return reply.code(201).send(result);
    } catch (error) {
      if (error instanceof AuthError) {
        return reply.code(error.statusCode).send({ message: error.message });
      }
      throw error;
    }
  });

  app.post<{ Body: AuthBody }>("/auth/login", async (req, reply) => {
    try {
      validateAuthBody(req.body);
      const result = await authService.login(req.body);
      return reply.code(200).send(result);
    } catch (error) {
      if (error instanceof AuthError) {
        return reply.code(error.statusCode).send({ message: error.message });
      }
      throw error;
    }
  });
}
