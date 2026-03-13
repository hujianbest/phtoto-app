# Task 17 校验记录（关注关系与关注流）

日期：2026-03-13

## 目标

- 新增用户关注关系接口；
- 提供关注流接口，返回“我关注的人”发布的作品；
- 保持推荐流不受影响。

## TDD 记录

### RED

先新增测试：`services/api/test/feed.following.test.ts`，覆盖：
- 创建两个作者的帖子；
- 建立 `viewer -> alice` 关注关系；
- 查询关注流仅返回 `alice` 的帖子。

执行：

```powershell
npm test -- feed.following.test.ts
```

结果：
- 失败（预期 RED）：`POST /social/follow` 返回 `404`。

### GREEN

最小实现：
- `services/api/src/modules/social/follow.service.ts`
- `services/api/src/modules/social/follow.routes.ts`（`POST /social/follow`）
- `services/api/src/modules/feed/feed.routes.ts` 新增 `GET /feed/following?email=...`
- `services/api/src/modules/posts/post.entity.ts|post.service.ts|post.routes.ts` 增加 `authorEmail` 承载作者归属
- `services/api/src/server.ts` 注册 follow 路由

再次执行：

```powershell
npm test -- feed.following.test.ts
```

结果：
- 通过（GREEN）。

## 回归验证

```powershell
npm run build
npm test -- feed.following.test.ts feed.recommendation.test.ts auth.register-login.test.ts
```

结果：
- 构建通过；
- 3 个测试套件、8 个测试全部通过。

## 结论

- 关注关系与关注流能力已具备；
- 推荐流和鉴权核心链路回归通过。
