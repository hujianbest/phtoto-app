# Task 13 校验记录（挑战入口、发现筛选、举报入口）

日期：2026-03-13

## 目标

- 为 Android 客户端补齐更接近可用社区产品的入口能力：
  - 每周挑战；
  - 发现页关键词筛选；
  - 作品举报提交。
- 在 API 侧补齐发现流关键词筛选能力。

## 关键改动

- Android：
  - `apps/android/app/src/main/java/com/photoapp/feed/FeedScreen.kt`
    - 新增挑战入口、筛选输入框、举报按钮与提示信息。
  - `apps/android/app/src/main/java/com/photoapp/challenge/WeeklyChallengeScreen.kt`
    - 新增每周挑战页面。
  - `apps/android/app/src/main/java/com/photoapp/report/ReportScreen.kt`
    - 新增举报提交页面。
  - `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
    - 新增 challenge/report 路由与导航；
    - 举报提交接入网络调用并回显提交结果。
  - `apps/android/app/src/main/java/com/photoapp/network/ApiClient.kt`
    - 新增 `createPostReport`；
    - 缩短网络超时以降低弱网等待。
  - `apps/android/app/src/androidTest/java/com/photoapp/TestSetup.kt`
    - `loginIfNeeded` 增加进入发现页等待，提升测试稳定性。

- API：
  - `services/api/src/modules/feed/feed.routes.ts`
    - `GET /feed/recommended` 增加 `keyword` 查询参数，支持按标题/描述/意图过滤后再排序。

## 校验命令与结果

### API

在 `services/api` 目录执行：

```powershell
npm run build
npm test -- feed.recommendation.test.ts moderation.report-flow.test.ts
```

结果：
- 构建通过；
- 2 个测试套件、4 个测试全部通过。

### Android 编译

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
```

结果：
- `BUILD SUCCESSFUL`。

## 说明

### Android 设备级回归

在已启动模拟器后执行：

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- `Starting 3 tests on photoappApi34(AVD) - 14`
- `Finished 3 tests on photoappApi34(AVD) - 14`
- `BUILD SUCCESSFUL`

## 说明

- 本次未新增仪器测试用例，但既有 Task 8-10 三条设备级用例在新改动后回归通过；
- 后续建议补充“挑战入口”和“举报流程”的仪器测试用例，覆盖新增功能主路径。
