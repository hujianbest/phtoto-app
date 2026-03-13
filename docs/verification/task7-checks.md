# Task 7 验证记录（举报与内容审核流程）

日期：2026-03-13

## RED -> GREEN（TDD）

### moderation + report flow
- RED
  - 命令：`cd services/api && npm test -- test/moderation.report-flow.test.ts`
  - 结果：失败（`/moderation/check` 与 `/reports` 返回 404，2/2 失败）
- GREEN
  - 最小实现：
    - `services/api/src/modules/moderation/moderation.service.ts`
    - `services/api/src/modules/moderation/moderation.routes.ts`
    - `services/api/src/modules/reports/report.entity.ts`
    - `services/api/src/modules/reports/report.routes.ts`
    - `services/api/src/server.ts`（注册 moderation/report 路由）
  - 命令：`cd services/api && npm test -- test/moderation.report-flow.test.ts`
  - 结果：通过（1 suite, 2 tests）

## 回归测试

### api（health/auth/posts/reviews/feed/moderation-report）
- 命令：`cd services/api && npm test -- test/health.test.ts test/auth.register-login.test.ts test/posts.create-with-exif.test.ts test/reviews.structured-review.test.ts test/feed.recommendation.test.ts test/moderation.report-flow.test.ts`
- 结果：通过（6 suites, 19 tests）

## 结论
- Task 7 最小实现已完成：审核分级（high/medium/low）+ 高风险拦截、举报创建（pending）、举报状态流转（reviewed/closed）与测试覆盖均已落地并回归通过。

## 质量收口补充（本轮）

- 审核关键词匹配优化：英文关键词按单词边界匹配，避免 `skill` 被误判为 `kill`。
- `/moderation/check` 增加输入长度上限（2000 字符）。
- `/reports` 增加字段长度校验：`targetId <= 128`、`reason <= 500`。
- 举报状态流转约束：
  - 禁止 `pending -> closed` 跳级
  - `closed` 后禁止再次变更
  - 非法流转返回 `409`
- 已在 `test/moderation.report-flow.test.ts` 补充边界断言并通过回归。
