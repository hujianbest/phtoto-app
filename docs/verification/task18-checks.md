# Task 18 校验记录（Android 推荐流/关注流切换接入）

日期：2026-03-13

## 目标

- Android 端接入新增关注流 API；
- 在发现页提供“推荐流 / 关注流”切换入口；
- 支持从发现卡片发起关注作者动作（依赖邮箱作者标识）。

## 代码变更

- `apps/android/app/src/main/java/com/photoapp/network/ApiClient.kt`
  - 新增 `fetchFollowingPosts(email)`；
  - 新增 `followAuthor(followerEmail, followeeEmail)`；
  - `createPost` 新增 `authorEmail` 上报；
  - 统一抽取 feed 解析逻辑。
- `apps/android/app/src/main/java/com/photoapp/post/PostRepository.kt`
  - 新增 `FeedMode`；
  - `syncFromRemote` 支持按模式同步推荐流/关注流；
  - `publish` 支持携带 `viewerEmail` 上传作者归属。
- `apps/android/app/src/main/java/com/photoapp/feed/FeedScreen.kt`
  - 新增推荐流/关注流切换按钮；
  - 新增“关注作者”按钮。
- `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
  - 增加 `feedMode` 状态；
  - 切换 tab 时触发对应远端同步；
  - 关注作者动作调用后端 follow 接口。

## 校验命令与结果

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- 编译通过；
- 设备级回归通过（5 tests，`BUILD SUCCESSFUL`）。

## 结论

- Android 已具备推荐/关注双流切换能力；
- 与 Task17 的 follow/following API 完成端到端联动。
