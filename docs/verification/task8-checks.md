# Task 8 校验记录（Android 客户端骨架与登录态）

日期：2026-03-13

## TDD 记录

### RED
- 已先编写失败测试：`apps/android/app/src/androidTest/java/com/photoapp/AuthFlowTest.kt`
- 执行命令（尝试）：
  - `gradle :app:connectedDebugAndroidTest`
  - `.\gradlew :app:connectedDebugAndroidTest`
- 结果：失败，原因不是断言失败，而是本机缺少可执行 Gradle（`gradle` 不存在）且项目无 `gradlew` 包装脚本。

### GREEN
- 已实现最小功能：
  - 登录页展示“登录页”文案和登录按钮（tag：`login_button`）
  - 点击登录按钮触发 `AuthViewModel.login()`
  - `AuthViewModel` 通过 DataStore Preferences 写入 token（`token=demo-token`）
  - `AppNavGraph` 监听登录态后导航至“发现页”
- 再次执行测试命令（尝试）：
  - `gradle :app:connectedDebugAndroidTest`
- 结果：仍因本机缺少 Gradle 而无法启动测试流程，无法获得通过态运行证据。

## 可执行性与阻塞

### 阻塞项
1. 本机未安装或未配置 Gradle 命令（`where gradle` 无结果）。
2. 当前 `apps/android` 下未提供 `gradlew`/`gradlew.bat` Wrapper。
3. 未验证到 Android SDK/模拟器可用性（在 Gradle 不可执行前已被前置阻塞）。

### 已保证的可编译结构（静态层面）
- 已补齐 Android 工程最小骨架：
  - `apps/android/settings.gradle.kts`
  - `apps/android/app/build.gradle.kts`
  - `apps/android/app/src/main/AndroidManifest.xml`
  - `apps/android/app/src/main/java/com/photoapp/MainActivity.kt`
  - `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
  - `apps/android/app/src/main/java/com/photoapp/auth/AuthViewModel.kt`
  - `apps/android/app/src/androidTest/java/com/photoapp/AuthFlowTest.kt`
  - `apps/android/app/proguard-rules.pro`

## 本机恢复建议（供后续验证）
1. 安装 Gradle 或在项目中生成/提交 Gradle Wrapper。
2. 安装 Android SDK + 至少一个可用模拟器并启动。
3. 执行：
   - `.\gradlew :app:assembleDebug`
   - `.\gradlew :app:connectedDebugAndroidTest`

## 复验更新（环境已打通）

日期：2026-03-13（同日补充）

### 环境打通结果
- 已补齐并验证：
  - Gradle Wrapper 可用（`apps/android/gradlew.bat`）
  - Android SDK / Emulator / ADB 可用
  - `local.properties` 与 `android.useAndroidX=true` 已配置

### 复验过程（含问题收敛）
1. 首次真实执行 `.\gradlew.bat connectedDebugAndroidTest`  
   - 失败：`auth.preferences_pb` 出现 `multiple DataStores active`
2. 修复：收敛 DataStore 定义与测试登录态处理
3. 再次执行  
   - 失败：登录后自动重定向条件过宽，影响非 discover 路由
4. 修复：`AppNavGraph` 仅在登录路由触发登录后跳转
5. 最终执行 `.\gradlew.bat connectedDebugAndroidTest`
   - 结果：`BUILD SUCCESSFUL`
   - 输出：`Finished 3 tests on photoappApi34(AVD) - 14`

### 结论
- Task 8 在设备级联调已复验通过（不再是环境阻塞态）。
