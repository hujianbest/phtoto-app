import { buildServer } from "./server";

async function start() {
  const port = Number(process.env.PORT ?? "3000");
  const host = process.env.HOST ?? "0.0.0.0";

  if (!process.env.JWT_SECRET || process.env.JWT_SECRET.trim().length === 0) {
    throw new Error("JWT_SECRET is required. Example: JWT_SECRET=dev_only_secret_for_local_run");
  }

  const app = buildServer();
  await app.listen({ port, host });
  app.log.info(`api listening on http://${host}:${port}`);
}

start().catch((error) => {
  console.error(error);
  process.exit(1);
});
