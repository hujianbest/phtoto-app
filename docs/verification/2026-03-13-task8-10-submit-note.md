# Task 8-10 可提交说明

日期：2026-03-13

## 变更摘要

- 打通 Android 本地执行链路，完成 `connectedDebugAndroidTest` 真实可运行验证。
- 修复 Android 仪器测试稳定性问题，消除 DataStore 多实例冲突。
- 修复登录后导航条件过宽问题，避免进入发帖/点评页后被错误重定向。
- 补齐 Task 8/9/10 的复验证据文档，状态由“环境阻塞”更新为“设备级通过”。

## 代码变更范围

- `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
  - 登录后自动跳转仅在 `login` 路由触发，避免覆盖业务路由。
- `apps/android/app/src/androidTest/java/com/photoapp/TestSetup.kt`
  - 增加统一测试辅助方法，处理“已登录/未登录”两种初始态。
- `apps/android/app/src/androidTest/java/com/photoapp/AuthFlowTest.kt`
  - 增强登录流断言容错与等待逻辑，降低偶发失败。
- `apps/android/app/src/androidTest/java/com/photoapp/PostPublishTest.kt`
  - 接入统一登录态处理，稳定发帖-回流断言。
- `apps/android/app/src/androidTest/java/com/photoapp/ReviewHelpfulTest.kt`
  - 接入统一登录态处理，稳定 helpful 计数断言。

## 文档更新范围

- `docs/verification/task8-checks.md`
- `docs/verification/task9-checks.md`
- `docs/verification/task10-checks.md`

更新内容包括：
- 环境打通事实（Wrapper/SDK/Emulator/ADB）
- 从失败到修复的关键链路
- 最终设备级执行结果（通过）

## 验证命令与结果

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

结果（最终）：
- `Finished 3 tests on photoappApi34(AVD) - 14`
- `BUILD SUCCESSFUL`

## 风险与注意事项

- 仍依赖本机 Android SDK/Emulator 环境；在新机器需先完成同等环境配置。
- `AuthFlowTest` 使用了轻量容错等待逻辑，若后续导航规则变化，需同步更新断言策略。
- 当前验证覆盖的是 Task 8-10 主链路；如新增复杂分支交互，建议补充对应仪器测试用例。

## 建议提交信息（可选）

`fix(android): stabilize instrumentation flow and verify task8-10 on device`
