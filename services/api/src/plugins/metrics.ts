import type { FastifyInstance } from "fastify";

export function registerMetricsPlugin(app: FastifyInstance) {
  let totalRequests = 0;
  let totalErrors = 0;
  let totalLatencyMs = 0;

  app.addHook("onResponse", async (request, reply) => {
    if (request.url.startsWith("/metrics")) {
      return;
    }

    totalRequests += 1;
    if (reply.statusCode >= 500) {
      totalErrors += 1;
    }

    const startedAt = Number((request as { __metricsStartMs?: number }).__metricsStartMs ?? Date.now());
    const latencyMs = Math.max(0, Date.now() - startedAt);
    totalLatencyMs += latencyMs;
  });

  app.addHook("onRequest", async (request) => {
    (request as { __metricsStartMs?: number }).__metricsStartMs = Date.now();
  });

  app.get("/metrics", async (_, reply) => {
    const availability = totalRequests === 0 ? 1 : 1 - totalErrors / totalRequests;
    const errorRate = totalRequests === 0 ? 0 : totalErrors / totalRequests;
    const latency = totalRequests === 0 ? 0 : totalLatencyMs / totalRequests;

    const metricsText = [
      "# HELP availability Service availability ratio in [0,1]",
      "# TYPE availability gauge",
      `availability ${availability.toFixed(6)}`,
      "# HELP latency Request latency in milliseconds",
      "# TYPE latency gauge",
      `latency ${latency.toFixed(3)}`,
      "# HELP error_rate Request error ratio in [0,1]",
      "# TYPE error_rate gauge",
      `error_rate ${errorRate.toFixed(6)}`,
      "",
    ].join("\n");

    reply.header("content-type", "text/plain; charset=utf-8");
    return reply.send(metricsText);
  });
}
