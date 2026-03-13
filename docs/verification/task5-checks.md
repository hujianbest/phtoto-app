# Task 5 Verification - 点评系统（普通评论 + 结构化点评）

日期: 2026-03-13

## 1) RED 阶段（先失败）

命令:

`cd services/api && npm test -- test/reviews.structured-review.test.ts`

关键输出摘要:

- `FAIL test/reviews.structured-review.test.ts`
- 用例: `POST /posts/:id/reviews creates structured review`
- 断言失败: `Expected: 201` / `Received: 404`
- 用例: `POST /reviews/:id/helpful increments helpfulCount`
- 断言失败: `Expected: 201` / `Received: 404`

结论: 在实现前，点评相关路由未注册，测试按预期失败。

## 2) GREEN 阶段（实现后通过）

命令:

`cd services/api && npm test -- test/reviews.structured-review.test.ts`

关键输出摘要:

- `PASS test/reviews.structured-review.test.ts`
- `Test Suites: 1 passed, 1 total`
- `Tests: 4 passed, 4 total`

结论: `POST /posts/:id/reviews` 与 `POST /reviews/:id/helpful` 最小实现通过新增测试。

## 3) 回归检查（health + auth + posts + reviews）

命令:

`cd services/api && npm test -- test/health.test.ts test/auth.register-login.test.ts test/posts.create-with-exif.test.ts test/reviews.structured-review.test.ts`

关键输出摘要:

- `PASS test/reviews.structured-review.test.ts`
- `PASS test/health.test.ts`
- `PASS test/posts.create-with-exif.test.ts`
- `PASS test/auth.register-login.test.ts`
- `Test Suites: 4 passed, 4 total`
- `Tests: 15 passed, 15 total`

结论: 健康检查、认证、帖子发布链路与新点评链路均通过，无明显回归。

## 4) MVP 说明（存储语义）

- 点评数据当前使用内存 `Map` 存储（`ReviewService`），用于最小可用链路验证。
- 服务重启后点评数据不会持久化。

## 5) 质量收口（本轮补充）

- 创建点评要求“comment 或有效 structured 至少一个”，空反馈返回 `400`。
- `structured` 分数字段只接受 `1-5` 的有限数值；无效分值不会构成有效结构化反馈。
- `POST /posts/:id/reviews` 新增帖子存在性校验，不存在帖子返回 `404`。
- `POST /reviews/:id/helpful` 引入基于 `x-client-id` 的最小幂等策略：同一客户端重复标记不重复加分。
