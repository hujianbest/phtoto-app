# Task 14 校验记录（用户信息/挑战状态/举报历史持久化）

日期：2026-03-13

## 目标

- 登录后持久化用户邮箱并在个人页展示；
- 持久化每周挑战参与状态并在挑战页/个人页可见；
- 持久化举报历史（本地会话），同时保持举报接口后端提交。

## 代码变更

- `apps/android/app/src/main/java/com/photoapp/auth/AuthViewModel.kt`
  - 扩展 `AuthUiState`：`email/challengeJoinedAt/reportHistory`；
  - 登录成功与离线兜底都保存 `email`；
  - 新增 `joinWeeklyChallenge()` 与 `addReportHistory()`；
  - 举报历史写入 DataStore（最多保留 20 条）。
- `apps/android/app/src/main/java/com/photoapp/challenge/WeeklyChallengeScreen.kt`
  - 增加参与状态展示与“参加挑战”按钮。
- `apps/android/app/src/main/java/com/photoapp/profile/ProfileScreen.kt`
  - 增加账号、挑战状态、举报数量与最近一条举报记录展示。
- `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
  - 把新状态透传到挑战页/个人页；
  - 举报提交时调用 `addReportHistory()` 持久化记录。

## 验证命令与结果

### API 回归

在 `services/api` 目录执行：

```powershell
npm run build
npm test -- auth.register-login.test.ts moderation.report-flow.test.ts
```

结果：
- 构建成功；
- 2 个测试套件、7 个测试全部通过。

### Android 编译回归

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
```

结果：
- `BUILD SUCCESSFUL`。

### Android 设备级回归

模拟器在线后执行：

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- `Finished 3 tests on photoappApi34(AVD) - 14`
- `BUILD SUCCESSFUL`

## 结论

- 新增持久化状态能力已落地且不影响现有 Task 8-10 主链路测试；
- 当前版本已具备“可用 + 可持续使用”的基础用户状态沉淀能力。
