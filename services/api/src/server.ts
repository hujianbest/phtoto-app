import Fastify from "fastify";
import { registerHealthRoute } from "./routes/health";
import { registerAuthRoutes } from "./modules/auth/auth.routes";
import { registerPostRoutes } from "./modules/posts/post.routes";
import { registerReviewRoutes } from "./modules/reviews/review.routes";
import { registerFeedRoutes } from "./modules/feed/feed.routes";
import { registerModerationRoutes } from "./modules/moderation/moderation.routes";
import { registerReportRoutes } from "./modules/reports/report.routes";
import { registerMetricsPlugin } from "./plugins/metrics";

export function buildServer() {
  const app = Fastify();
  registerHealthRoute(app);
  registerMetricsPlugin(app);
  registerAuthRoutes(app);
  registerPostRoutes(app);
  registerReviewRoutes(app);
  registerFeedRoutes(app);
  registerModerationRoutes(app);
  registerReportRoutes(app);
  return app;
}
