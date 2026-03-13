# 摄影社交 APP（Android 首发）Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 构建一个面向进阶摄影爱好者的 Android 社区 APP，支持作品发布、EXIF 展示、结构化点评、发现流和基础风控治理。  
**Architecture:** 采用单仓多目录结构：`apps/android`（Jetpack Compose 客户端）+ `services/api`（Node.js API）+ `services/recommendation`（排序与特征计算）+ `packages/shared`（共享类型与接口约束）。先保证 MVP 端到端闭环，再优化推荐与运营能力。  
**Tech Stack:** Kotlin + Jetpack Compose、Node.js + Fastify + PostgreSQL、Redis、MinIO/S3 兼容对象存储、OpenAPI、Jest + Vitest + Android Instrumentation、Docker Compose

---

### Task 1: 初始化仓库结构与开发基线

**Files:**
- Create: `README.md`
- Create: `docs/architecture/system-overview.md`
- Create: `docker-compose.yml`
- Create: `.editorconfig`
- Create: `.gitignore`

**Step 1: 写失败检查（目录与文件存在性）**

```bash
test -f README.md && test -f docker-compose.yml
```

**Step 2: 运行检查确保失败**

Run: `test -f README.md && test -f docker-compose.yml; echo $?`  
Expected: `1`

**Step 3: 最小实现**

- 写入仓库说明、服务边界、启动步骤。
- 建立 `postgres/redis/minio` 的 compose 服务定义。

**Step 4: 再次验证通过**

Run: `test -f README.md && test -f docker-compose.yml; echo $?`  
Expected: `0`

**Step 5: Commit**

```bash
git add README.md docs/architecture/system-overview.md docker-compose.yml .editorconfig .gitignore
git commit -m "chore: initialize monorepo baseline for photo social app"
```

---

### Task 2: API 服务骨架与健康检查

**Files:**
- Create: `services/api/package.json`
- Create: `services/api/src/server.ts`
- Create: `services/api/src/routes/health.ts`
- Create: `services/api/test/health.test.ts`

**Step 1: 写失败测试**

```ts
import request from "supertest";
import { buildServer } from "../src/server";

it("GET /health returns ok", async () => {
  const app = buildServer();
  const res = await request(app.server).get("/health");
  expect(res.status).toBe(200);
  expect(res.body.status).toBe("ok");
});
```

**Step 2: 运行测试确保失败**

Run: `cd services/api && npm test -- health.test.ts`  
Expected: FAIL with module not found / buildServer undefined

**Step 3: 最小实现**

```ts
// services/api/src/server.ts
import Fastify from "fastify";
import { registerHealthRoute } from "./routes/health";

export function buildServer() {
  const app = Fastify();
  registerHealthRoute(app);
  return app;
}
```

**Step 4: 运行测试确保通过**

Run: `cd services/api && npm test -- health.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/api
git commit -m "feat(api): bootstrap fastify service with health endpoint"
```

---

### Task 3: 鉴权与用户基础模型

**Files:**
- Create: `services/api/src/modules/auth/auth.routes.ts`
- Create: `services/api/src/modules/auth/auth.service.ts`
- Create: `services/api/src/modules/users/user.entity.ts`
- Create: `services/api/test/auth.register-login.test.ts`
- Modify: `services/api/src/server.ts`

**Step 1: 写失败测试**

```ts
it("register then login returns token", async () => {
  // register -> login -> receive JWT
});
```

**Step 2: 验证失败**

Run: `cd services/api && npm test -- auth.register-login.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- 增加 `POST /auth/register`、`POST /auth/login`
- 密码哈希（bcrypt）
- 返回 JWT（含 userId）

**Step 4: 验证通过**

Run: `cd services/api && npm test -- auth.register-login.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/api/src/modules services/api/test
git commit -m "feat(api): add registration and login with jwt"
```

---

### Task 4: 作品发布与 EXIF 抽取链路

**Files:**
- Create: `services/api/src/modules/posts/post.routes.ts`
- Create: `services/api/src/modules/posts/post.service.ts`
- Create: `services/api/src/modules/posts/post.entity.ts`
- Create: `services/api/src/modules/posts/exif.extractor.ts`
- Create: `services/api/test/posts.create-with-exif.test.ts`

**Step 1: 写失败测试**

```ts
it("create post stores exif fields", async () => {
  // upload image metadata -> assert aperture/iso/shutter persisted
});
```

**Step 2: 验证失败**

Run: `cd services/api && npm test -- posts.create-with-exif.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- `POST /posts` 支持标题、描述、图片 URL、创作意图
- 通过 `exif.extractor.ts` 解析参数并入库

**Step 4: 验证通过**

Run: `cd services/api && npm test -- posts.create-with-exif.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/api/src/modules/posts services/api/test/posts.create-with-exif.test.ts
git commit -m "feat(api): add post creation with exif extraction"
```

---

### Task 5: 点评系统（普通评论 + 结构化点评）

**Files:**
- Create: `services/api/src/modules/reviews/review.routes.ts`
- Create: `services/api/src/modules/reviews/review.service.ts`
- Create: `services/api/src/modules/reviews/review.entity.ts`
- Create: `services/api/test/reviews.structured-review.test.ts`
- Modify: `services/api/src/server.ts`

**Step 1: 写失败测试**

```ts
it("structured review can be marked helpful", async () => {
  // create review -> mark helpful -> assert score change
});
```

**Step 2: 验证失败**

Run: `cd services/api && npm test -- reviews.structured-review.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- `POST /posts/:id/reviews`
- `POST /reviews/:id/helpful`
- 结构化字段：composition/light/color/story/postprocess

**Step 4: 验证通过**

Run: `cd services/api && npm test -- reviews.structured-review.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/api/src/modules/reviews services/api/test/reviews.structured-review.test.ts services/api/src/server.ts
git commit -m "feat(api): add structured review and helpful feedback"
```

---

### Task 6: 发现流与推荐分层

**Files:**
- Create: `services/recommendation/package.json`
- Create: `services/recommendation/src/scoring.ts`
- Create: `services/recommendation/src/index.ts`
- Create: `services/recommendation/test/scoring.test.ts`
- Create: `services/api/src/modules/feed/feed.routes.ts`
- Create: `services/api/test/feed.recommendation.test.ts`

**Step 1: 写失败测试**

```ts
it("higher quality score ranks first", () => {
  // score(posts) -> sorted result
});
```

**Step 2: 验证失败**

Run: `cd services/recommendation && npm test -- scoring.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- 实现 `qualityScore + reviewScore + freshnessScore`
- API 提供 `GET /feed/recommended`

**Step 4: 验证通过**

Run: `cd services/recommendation && npm test -- scoring.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/recommendation services/api/src/modules/feed services/api/test/feed.recommendation.test.ts
git commit -m "feat(feed): add recommendation scoring and feed endpoint"
```

---

### Task 7: 举报与内容审核流程

**Files:**
- Create: `services/api/src/modules/moderation/moderation.routes.ts`
- Create: `services/api/src/modules/moderation/moderation.service.ts`
- Create: `services/api/src/modules/reports/report.routes.ts`
- Create: `services/api/src/modules/reports/report.entity.ts`
- Create: `services/api/test/moderation.report-flow.test.ts`

**Step 1: 写失败测试**

```ts
it("high-risk content should be blocked", async () => {
  // submit content with flagged tags -> blocked
});
```

**Step 2: 验证失败**

Run: `cd services/api && npm test -- moderation.report-flow.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- 审核分级（高/中/低风险）
- 举报入口（作品/评论/用户）
- 工单状态流转（pending/reviewed/closed）

**Step 4: 验证通过**

Run: `cd services/api && npm test -- moderation.report-flow.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add services/api/src/modules/moderation services/api/src/modules/reports services/api/test/moderation.report-flow.test.ts
git commit -m "feat(moderation): add risk review and report workflow"
```

---

### Task 8: Android 客户端骨架与登录态

**Files:**
- Create: `apps/android/settings.gradle.kts`
- Create: `apps/android/app/build.gradle.kts`
- Create: `apps/android/app/src/main/java/com/photoapp/MainActivity.kt`
- Create: `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
- Create: `apps/android/app/src/main/java/com/photoapp/auth/AuthViewModel.kt`
- Create: `apps/android/app/src/androidTest/java/com/photoapp/AuthFlowTest.kt`

**Step 1: 写失败测试**

```kotlin
@Test
fun loginSuccess_navigateToFeed() {
    // launch app -> submit login -> assert feed screen visible
}
```

**Step 2: 验证失败**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*AuthFlowTest*"`  
Expected: FAIL

**Step 3: 最小实现**

- Compose 导航：登录页 -> 发现页
- Token 本地存储（DataStore）

**Step 4: 验证通过**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*AuthFlowTest*"`  
Expected: PASS

**Step 5: Commit**

```bash
git add apps/android
git commit -m "feat(android): bootstrap compose app with auth flow"
```

---

### Task 9: Android 发布与发现流 UI

**Files:**
- Create: `apps/android/app/src/main/java/com/photoapp/feed/FeedScreen.kt`
- Create: `apps/android/app/src/main/java/com/photoapp/post/CreatePostScreen.kt`
- Create: `apps/android/app/src/main/java/com/photoapp/post/PostRepository.kt`
- Create: `apps/android/app/src/androidTest/java/com/photoapp/PostPublishTest.kt`

**Step 1: 写失败测试**

```kotlin
@Test
fun publishPost_showsInFeed() {
    // create post -> open feed -> assert card visible
}
```

**Step 2: 验证失败**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*PostPublishTest*"`  
Expected: FAIL

**Step 3: 最小实现**

- 发布页支持图片、标题、创作意图
- 发现页展示卡片（图、参数、作者）

**Step 4: 验证通过**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*PostPublishTest*"`  
Expected: PASS

**Step 5: Commit**

```bash
git add apps/android/app/src/main/java/com/photoapp/feed apps/android/app/src/main/java/com/photoapp/post apps/android/app/src/androidTest/java/com/photoapp/PostPublishTest.kt
git commit -m "feat(android): add publish flow and feed rendering"
```

---

### Task 10: 点评交互与个人主页

**Files:**
- Create: `apps/android/app/src/main/java/com/photoapp/review/ReviewSheet.kt`
- Create: `apps/android/app/src/main/java/com/photoapp/profile/ProfileScreen.kt`
- Create: `apps/android/app/src/androidTest/java/com/photoapp/ReviewHelpfulTest.kt`

**Step 1: 写失败测试**

```kotlin
@Test
fun markReviewHelpful_updatesCounter() {
    // click helpful -> counter increments
}
```

**Step 2: 验证失败**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*ReviewHelpfulTest*"`  
Expected: FAIL

**Step 3: 最小实现**

- 结构化点评表单组件
- “有帮助”按钮与计数回显
- 个人主页展示作品与成长指标

**Step 4: 验证通过**

Run: `cd apps/android && ./gradlew connectedAndroidTest --tests "*ReviewHelpfulTest*"`  
Expected: PASS

**Step 5: Commit**

```bash
git add apps/android/app/src/main/java/com/photoapp/review apps/android/app/src/main/java/com/photoapp/profile apps/android/app/src/androidTest/java/com/photoapp/ReviewHelpfulTest.kt
git commit -m "feat(android): add structured reviews and profile growth view"
```

---

### Task 11: 可观测性、灰度与上线清单

**Files:**
- Create: `docs/release/mvp-go-live-checklist.md`
- Create: `docs/ops/monitoring-dashboard-spec.md`
- Create: `services/api/src/plugins/metrics.ts`
- Create: `services/api/test/metrics.health-sli.test.ts`

**Step 1: 写失败测试**

```ts
it("metrics endpoint exposes availability and latency", async () => {
  // assert /metrics includes required labels
});
```

**Step 2: 验证失败**

Run: `cd services/api && npm test -- metrics.health-sli.test.ts`  
Expected: FAIL

**Step 3: 最小实现**

- 接入 API 指标（可用性、延迟、错误率）
- 定义灰度发布步骤与回滚策略

**Step 4: 验证通过**

Run: `cd services/api && npm test -- metrics.health-sli.test.ts`  
Expected: PASS

**Step 5: Commit**

```bash
git add docs/release/mvp-go-live-checklist.md docs/ops/monitoring-dashboard-spec.md services/api/src/plugins/metrics.ts services/api/test/metrics.health-sli.test.ts
git commit -m "chore(release): add mvp go-live checklist and api metrics"
```

---

## 依赖技能引用

- 计划执行：`@superpowers/executing-plans`
- 子代理实现（可选）：`@superpowers/subagent-driven-development`
- 完工前验证：`@superpowers/verification-before-completion`
- 请求代码评审：`@superpowers/requesting-code-review`

---

## 完成定义（Definition of Done）

- Android 端可完成：登录、发帖、浏览、点评、个人页、举报
- API 通过核心单元与集成测试
- 关键 SLI 可观测（可用性/延迟/错误率）
- 上线清单可执行且包含灰度与回滚策略
- 设计指标可追踪：高质量点评占比、帮助率、7/30 日留存
