# 阶段性测试报告（Phase Test Report）

日期：2026-03-13  
阶段：可用化迭代（Task 12 - Task 21）

## 1. 测试目标

- 验证当前版本是否满足“可用 APP”阶段要求：
  - Android 核心链路可运行；
  - API 核心能力可用并通过回归；
  - 新增能力（挑战、举报历史、关注流、结构化筛选）稳定。

## 2. 测试范围

### API（Node + Fastify）

- 鉴权：注册/登录
- 发帖与 EXIF
- 结构化点评与 helpful
- 推荐流（含结构化筛选）
- 关注关系与关注流
- 举报流程与举报历史查询
- 每周挑战参与
- `/metrics` 可观测性接口

### Android（Compose + Instrumentation）

- 登录流
- 发帖回流
- 点评 helpful
- 挑战参与
- 举报流程
- 关注作者并切换关注流

## 3. 执行命令与结果

### API 构建与回归

执行目录：`services/api`

```powershell
npm run build
npm test -- auth.register-login.test.ts posts.create-with-exif.test.ts reviews.structured-review.test.ts feed.recommendation.test.ts feed.following.test.ts moderation.report-flow.test.ts reports.history.test.ts challenge.weekly.test.ts metrics.health-sli.test.ts
```

结果：
- 构建：通过
- 测试：`Test Suites: 9 passed, 9 total`
- 测试：`Tests: 26 passed, 26 total`

### Android 编译与设备回归

执行目录：`apps/android`

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- 编译：通过
- 设备测试：`Starting 6 tests ...`
- 设备测试：`Finished 6 tests ...`
- 构建：`BUILD SUCCESSFUL`

## 4. 阶段结论

- 当前版本核心能力已达到“可用 + 可持续使用”阶段目标。
- 关键新增能力（关注流、挑战、举报历史后端化、结构化筛选）均已接入并通过回归。
- 目前未发现阻断级故障；测试结果可支持继续迭代下一阶段功能。

## 5. 已知风险与建议

- 推荐流结构化筛选目前主要在 API 层生效，Android 端筛选 UI 仍可继续增强为多条件控件。
- 关注作者当前依赖作者邮箱作为关注标识，后续建议引入稳定 `authorId`。
- 举报历史目前按举报人邮箱查询，后续建议引入鉴权上下文避免依赖 query 参数。
