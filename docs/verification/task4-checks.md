# Task 4 Verification - 作品发布与 EXIF 抽取链路

日期: 2026-03-13

## 1) 初版 RED 阶段（先失败）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `FAIL test/posts.create-with-exif.test.ts`
- 用例: `POST /posts creates post and stores normalized exif fields`
- 断言失败: `Expected: 201` / `Received: 404`

结论: 在实现前，`POST /posts` 路由不存在，测试按预期失败。

## 2) 初版 GREEN 阶段（实现后通过）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `PASS test/posts.create-with-exif.test.ts`
- `Test Suites: 1 passed, 1 total`
- `Tests: 1 passed, 1 total`

结论: `POST /posts` + EXIF 规范化最小实现可通过新增测试。

## 3) 审查修复 RED 阶段（新增负例先失败）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `FAIL test/posts.create-with-exif.test.ts`
- 失败用例 1: `POST /posts returns 400 for non-http imageUrl`
- 失败信息: `Expected: 400` / `Received: 201`
- 失败用例 2: `POST /posts ignores invalid exif numeric fields`
- 失败信息: 返回中包含被宽松截断后的 `aperture` 与 `iso`

结论: 审查指出的问题被测试有效捕获（URL 校验不足、EXIF 数字解析过于宽松）。

## 4) 审查修复 GREEN 阶段（负例通过）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `PASS test/posts.create-with-exif.test.ts`
- `Tests: 5 passed, 5 total`
- 覆盖新增用例:
  - 缺字段/空字符串 -> `400`
  - 非法 `imageUrl` -> `400`
  - 非法 EXIF 数字值 -> 忽略字段（不入返回 `exif`）

结论: 审查修复点已通过单测验证。

## 5) 回归检查（health + auth + posts）

命令:

`cd services/api && npm test -- health.test.ts auth.register-login.test.ts posts.create-with-exif.test.ts`

关键输出摘要:

- `PASS test/posts.create-with-exif.test.ts`
- `PASS test/health.test.ts`
- `PASS test/auth.register-login.test.ts`
- `Test Suites: 3 passed, 3 total`
- `Tests: 10 passed, 10 total`

结论: 现有健康检查与认证链路未出现回归。

## 6) MVP 范围说明（存储语义）

- 当前 `POST /posts` 的“入库”是内存存储（`PostService` 内 `Map`），用于 MVP 阶段链路验证。
- 尚未接入持久化数据库；服务重启后帖子数据会丢失。

## 7) Task4 质量复审（本轮最小修复）

### 7.1 可审计锚点

- 执行时间（本机）: `2026-03-13 22:42:40 +08:00`
- 会话环境元信息标注: `Is directory a git repo: No`
- 本轮未执行任何 `git commit` / `git push`
- 说明: 本项目当前按会话元信息记录为非 Git 仓库，因此本轮不包含 commit 级别审计锚点。

### 7.2 RED（新增入库副作用断言后先失败）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `FAIL test/posts.create-with-exif.test.ts`
- TypeScript 错误: `Property 'getById' does not exist on type 'PostService'`

结论: 测试先失败，证明新增“入库副作用断言”有效约束了缺失能力。

### 7.3 GREEN（补齐最小实现后通过）

命令:

`cd services/api && npm test -- posts.create-with-exif.test.ts`

关键输出摘要:

- `PASS test/posts.create-with-exif.test.ts`
- `Tests: 5 passed, 5 total`
- 成功用例已覆盖:
  - 响应体 EXIF 匹配
  - `postService.getById(id)` 可读到内存 `Map` 中对应记录且 EXIF 匹配

### 7.4 回归检查

命令:

`cd services/api && npm test -- health.test.ts auth.register-login.test.ts posts.create-with-exif.test.ts`

关键输出摘要:

- `PASS test/health.test.ts`
- `PASS test/auth.register-login.test.ts`
- `PASS test/posts.create-with-exif.test.ts`
- `Test Suites: 3 passed, 3 total`
- `Tests: 11 passed, 11 total`
