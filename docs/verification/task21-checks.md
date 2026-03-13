# Task 21 校验记录（推荐流结构化筛选）

日期：2026-03-13

## 目标

- 让 `GET /feed/recommended` 支持结构化筛选：
  - `genre`
  - `gearBrand`
  - `city`
  - `challengeTag`
- 保持现有关键词筛选和推荐排序能力不受影响。

## TDD 记录

### RED

先在 `services/api/test/feed.recommendation.test.ts` 新增用例：
- 创建两条带不同 `metadata` 的帖子；
- 用 `city=shanghai&challengeTag=night` 查询；
- 期望仅返回命中帖子。

执行：

```powershell
npm test -- feed.recommendation.test.ts
```

结果：
- 失败（预期 RED）：未命中的帖子仍出现在结果里。

### GREEN

最小实现：
- `services/api/src/modules/posts/post.entity.ts`
  - 新增 `PostMetadata`，帖子增加 `metadata` 字段。
- `services/api/src/modules/posts/post.service.ts`
  - 持久化 `metadata`（小写归一化字段）。
- `services/api/src/modules/posts/post.routes.ts`
  - `metadata` 结构校验（字段白名单 + 字符串/长度约束）。
- `services/api/src/modules/feed/feed.routes.ts`
  - 在推荐流中新增结构化过滤条件，与关键词条件并行生效。

再次执行：

```powershell
npm test -- feed.recommendation.test.ts
```

结果：
- 通过（GREEN）。

## 回归验证

```powershell
npm run build
npm test -- auth.register-login.test.ts posts.create-with-exif.test.ts reviews.structured-review.test.ts feed.recommendation.test.ts feed.following.test.ts moderation.report-flow.test.ts reports.history.test.ts challenge.weekly.test.ts metrics.health-sli.test.ts
```

结果：
- 构建通过；
- 9 个测试套件、26 个测试全部通过。

## 结论

- 推荐流已具备“关键词 + 结构化维度”组合过滤能力；
- 推荐排序、鉴权、发帖、点评、举报、挑战、可观测性主链路回归均通过。
