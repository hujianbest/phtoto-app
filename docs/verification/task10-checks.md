# Task 10 Checks - 点评交互与个人主页

日期：2026-03-13  
范围：`ReviewSheet`、`ProfileScreen`、`ReviewHelpfulTest`、导航接入

## TDD 记录

### RED（先写测试，再执行）
- 新增测试：`apps/android/app/src/androidTest/java/com/photoapp/ReviewHelpfulTest.kt`
- 目标行为：进入点评页后，点击 `helpful` 按钮，计数从 `0` 变为 `1`
- 执行命令：
  - `gradle :app:connectedDebugAndroidTest --tests "com.photoapp.ReviewHelpfulTest"`
- 实际结果：**阻塞（环境）**
  - `gradle` 命令不可用（`CommandNotFoundException`）
  - 因此当前无法在本机得到“业务失败型 RED”输出，只拿到“环境阻塞型 RED”

### GREEN（补最小实现后再执行）
- 已补最小实现：
  - `ReviewSheet` 增加结构化字段与 helpful 计数交互
  - `ProfileScreen` 增加作品数量与成长指标展示
  - 导航接入 discover -> review/profile
- 再次执行同一命令：
  - `gradle :app:connectedDebugAndroidTest --tests "com.photoapp.ReviewHelpfulTest"`
- 实际结果：**仍阻塞（同上）**
  - 受限于本机 Android/Gradle 运行环境，无法产出可执行 GREEN 结论

## 环境阻塞项

1. `apps/android` 目录下缺少 `gradlew` / `gradlew.bat`
2. 系统未安装可直接调用的 `gradle` 命令（PATH 未配置或未安装）
3. 在未恢复构建工具前，无法执行 `connectedDebugAndroidTest`

## 可执行恢复步骤

1. 在 Android 工程根目录生成或补齐 Gradle Wrapper
   - 推荐在 IDE 中对工程执行 `gradle wrapper`
   - 预期产物：`gradlew`、`gradlew.bat`、`gradle/wrapper/*`
2. 安装并配置 Android SDK（含 platform-tools、build-tools、对应 API）
3. 设置环境变量（Windows）
   - `ANDROID_HOME` 或 `ANDROID_SDK_ROOT`
   - 将 `%ANDROID_HOME%\platform-tools` 加入 `PATH`
4. 启动模拟器或连接真机，确保 `adb devices` 可见设备
5. 在 `apps/android` 重新执行：
   - `./gradlew.bat :app:connectedDebugAndroidTest --tests "com.photoapp.ReviewHelpfulTest"`
6. 若希望先做 JVM 级验证，可补充 `test` 类型的纯单元测试，再执行：
   - `./gradlew.bat :app:testDebugUnitTest`
