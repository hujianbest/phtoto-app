# Task 16 校验记录（挑战参与后端化）

日期：2026-03-13

## 目标

- 新增挑战参与 API，并让 Android 挑战参与优先走后端；
- 保持离线场景兜底，不影响当前可用性。

## 代码变更

- API：
  - `services/api/src/modules/challenges/challenge.service.ts`
  - `services/api/src/modules/challenges/challenge.routes.ts`
  - `services/api/src/server.ts`（注册挑战路由）
  - `services/api/test/challenge.weekly.test.ts`
- Android：
  - `apps/android/app/src/main/java/com/photoapp/network/ApiClient.kt`
    - 新增 `joinWeeklyChallenge(email)` 调用 `/challenges/weekly/join`
  - `apps/android/app/src/main/java/com/photoapp/auth/AuthViewModel.kt`
    - `joinWeeklyChallenge()` 优先使用后端返回的 `joinedAt`，失败时本地兜底。

## 校验命令与结果

### API

在 `services/api` 执行：

```powershell
npm run build
npm test -- challenge.weekly.test.ts feed.recommendation.test.ts
```

结果：
- 构建通过；
- 2 个测试套件、4 个测试全部通过。

### Android

在 `apps/android` 执行：

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- 编译通过；
- 仪器测试通过（`Finished 5 tests ...`，`BUILD SUCCESSFUL`）。

## 结论

- 挑战参与能力已具备后端状态承载；
- Android 在有网时走服务端，无网时保持可用兜底。
