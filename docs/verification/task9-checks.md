# Task 9 校验记录（Android 发布与发现流 UI）

日期：2026-03-13

## TDD 记录

### RED
- 已先编写失败测试：`apps/android/app/src/androidTest/java/com/photoapp/PostPublishTest.kt`
- 测试覆盖链路：create post -> open feed -> assert card visible
- 执行命令（尝试）：
  - `gradle :app:connectedDebugAndroidTest --tests com.photoapp.PostPublishTest`
- 结果：命令未执行测试，报错 `gradle` 未安装或未配置为可执行命令（CommandNotFoundException）。

### GREEN
- 已实现最小功能并接入导航：
  - `CreatePostScreen`：可输入标题、创作意图、图片地址、参数摘要、作者并点击发布
  - `PostRepository`：内存实现（进程内列表）保存帖子
  - `FeedScreen`：展示发布后的帖子卡片（图、参数、作者）并提供进入发布页入口
  - `AppNavGraph`：登录后进入发现流，发现页可跳转发布页，发布后返回发现页
- 再次执行命令（尝试）：
  - `gradle :app:connectedDebugAndroidTest --tests com.photoapp.PostPublishTest`
  - `.\gradlew :app:connectedDebugAndroidTest --tests com.photoapp.PostPublishTest`
- 结果：仍被环境阻塞，`gradle` 与 `.\gradlew` 均不可执行（前者未安装，后者文件不存在）。

## 可执行性与阻塞

### 阻塞项
1. 本机未安装或未配置 `gradle` 命令。
2. `apps/android` 目录下缺少 `gradlew` / `gradlew.bat` Wrapper。
3. 因构建入口缺失，无法继续验证 Android SDK/模拟器层面的联调执行。

### 已保证的 Kotlin/Compose 结构完整性（静态实现）
- 已新增并连通以下文件：
  - `apps/android/app/src/main/java/com/photoapp/post/PostRepository.kt`
  - `apps/android/app/src/main/java/com/photoapp/post/CreatePostScreen.kt`
  - `apps/android/app/src/main/java/com/photoapp/feed/FeedScreen.kt`
  - `apps/android/app/src/androidTest/java/com/photoapp/PostPublishTest.kt`
- 已更新导航文件：
  - `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
- 结构上满足 Task 9 要求的发布与发现流 UI 最小闭环。
- 说明：受环境阻塞（Gradle/Wrapper 不可用），当前为静态结构与代码级闭环，尚未取得设备级 PASS 证据。

## 后续环境恢复建议
1. 安装 Gradle 或提交 Gradle Wrapper（`gradlew` / `gradlew.bat`）。
2. 配置 Android SDK 与可用模拟器后执行：
   - `.\gradlew :app:assembleDebug`
   - `.\gradlew :app:connectedDebugAndroidTest --tests com.photoapp.PostPublishTest`

## 复验更新（环境已打通）

日期：2026-03-13（同日补充）

### 复验命令
- `.\gradlew.bat connectedDebugAndroidTest`

### 复验过程（Task 9 相关）
1. 真实设备执行早期失败：
   - 现象：`create_post_title_input` 等节点无法找到
2. 根因定位：
   - 登录后路由跳转逻辑过宽，进入发布页后被强制拉回发现页
3. 修复：
   - 调整 `AppNavGraph` 的登录后跳转条件，仅在 `login` 路由触发
4. 回归执行：
   - `PostPublishTest` 通过（发帖 -> 回发现页 -> 卡片可见）

### 最终结果
- 全量仪器测试通过：`BUILD SUCCESSFUL`
- Task 9 的发布与发现流 UI 已完成设备级验证。
