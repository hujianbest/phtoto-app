# Task 6 验证记录（发现流与推荐分层）

日期：2026-03-13

## RED -> GREEN（TDD）

### 1) recommendation scoring
- RED
  - 命令：`cd services/recommendation && npm test -- test/scoring.test.ts`
  - 结果：失败（`Cannot find module '../src/scoring'`）
- GREEN
  - 新增最小实现：`services/recommendation/src/scoring.ts`、`services/recommendation/src/index.ts`
  - 命令：`cd services/recommendation && npm test -- test/scoring.test.ts`
  - 结果：通过（1/1）

### 2) API feed recommendation
- RED
  - 命令：`cd services/api && npm test -- test/feed.recommendation.test.ts`
  - 结果：失败（`GET /feed/recommended` 返回 404）
- GREEN
  - 新增最小实现：`services/api/src/modules/feed/feed.routes.ts` 并在 `server.ts` 注册
  - 命令：`cd services/api && npm test -- test/feed.recommendation.test.ts`
  - 结果：通过（2/2）

## 回归测试

### recommendation
- 命令：`cd services/recommendation && npm test -- test/scoring.test.ts`
- 结果：通过（1 suite, 3 tests）

### api build
- 命令：`cd services/api && npm run build`
- 结果：通过（`tsc -p tsconfig.build.json` 退出码 0）

### api（health/auth/posts/reviews/feed）
- 命令：`cd services/api && npm test -- test/health.test.ts test/auth.register-login.test.ts test/posts.create-with-exif.test.ts test/reviews.structured-review.test.ts test/feed.recommendation.test.ts`
- 结果：通过（5 suites, 17 tests）

## 结论
- Task 6 最小实现已完成：推荐评分分层 + 发现流推荐接口 + 对应测试与回归均通过。

## 质量收口补充（本轮）

- API 不再直接引用 `services/recommendation/src/*` 源码，改为依赖 `recommendation` 本地包（`file:../recommendation`），消除跨服务源码耦合与构建错误。
- 特征计算与排序统一由 `services/recommendation` 提供，API 仅负责聚合输入与输出映射，避免双实现漂移。
- `freshnessScore` 已切换为基于时间衰减的稳定计算（7 天半衰期），避免“按批次名次归一化”导致的全量漂移。
- `/feed/recommended` 返回解释字段：`qualityScore`、`reviewScore`、`freshnessScore`、`totalScore`、`weightVersion`。
- 本轮关键验证：
  - `cd services/recommendation && npm run build` 通过
  - `cd services/api && npm run build` 通过
  - `cd services/api && npm test -- ...feed.recommendation.test.ts` 通过（5 suites, 17 tests）
