# Task 15 校验记录（挑战流与举报流仪器测试补齐）

日期：2026-03-13

## 目标

- 为新增可用功能补齐设备级自动回归：
  - 每周挑战参与流程；
  - 举报提交流程（从发现页到个人页统计区）。

## 新增测试

- `apps/android/app/src/androidTest/java/com/photoapp/ChallengeFlowTest.kt`
  - 登录后进入挑战页，触发参加挑战并校验“已参与”状态可见。
- `apps/android/app/src/androidTest/java/com/photoapp/ReportFlowTest.kt`
  - 登录后发布测试帖子，进入举报页提交原因，再进入个人页校验举报区块可见。

## 校验命令与结果

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat :app:compileDebugAndroidTestKotlin
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- `compileDebugAndroidTestKotlin` 成功；
- `connectedDebugAndroidTest` 成功；
- 输出 `Starting 5 tests ...`，`Finished 5 tests ...`，`BUILD SUCCESSFUL`。

## 结论

- 新增挑战与举报功能已纳入设备级自动回归；
- 当前仪器测试总数由 3 提升至 5，覆盖面进一步完善。
