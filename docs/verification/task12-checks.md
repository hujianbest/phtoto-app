# Task 12 校验记录（Android 与 API 端到端基础联通）

日期：2026-03-13

## 目标

- 让 API 服务具备可直接启动入口；
- 让 Android 登录/发帖/发现流优先走后端接口；
- 后端不可达时保留可演示的离线兜底能力。

## 变更点

- `services/api/src/main.ts`
  - 新增 API 启动入口（`PORT/HOST/JWT_SECRET`）。
- `services/api/package.json`
  - 新增 `dev/start` 脚本和 `tsx` 依赖。
- `apps/android/app/src/main/java/com/photoapp/network/ApiClient.kt`
  - 新增轻量 HTTP 客户端，接入 `/auth/login|register`、`/posts`、`/feed/recommended`。
- `apps/android/app/src/main/java/com/photoapp/auth/AuthViewModel.kt`
  - 登录改为邮箱密码；网络失败时进入离线演示模式。
- `apps/android/app/src/main/java/com/photoapp/post/PostRepository.kt`
  - 发帖与发现流增加远端同步能力，并保留本地体验。
- `apps/android/app/src/main/java/com/photoapp/navigation/AppNavGraph.kt`
  - 登录页补齐输入框，登录后触发远端同步。
- `apps/android/app/build.gradle.kts`
  - 新增 `BuildConfig.API_BASE_URL`，启用 `buildConfig`。
- `apps/android/app/src/main/AndroidManifest.xml`
  - 增加网络权限，允许本地明文调试流量。

## 校验命令与结果

### API 构建与回归测试

在 `services/api` 目录执行：

```powershell
npm run build
npm test -- health.test.ts auth.register-login.test.ts feed.recommendation.test.ts
```

结果：
- `BUILD/TEST` 通过；
- `3` 个测试套件全部通过，`8` 个测试全部通过。

### Android 编译校验

在 `apps/android` 目录执行：

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
```

结果：
- `BUILD SUCCESSFUL`。

### Android 仪器测试回归

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- 当前环境失败，报错 `No connected devices!`；
- 属于执行环境问题（无已连接模拟器/设备），非编译或代码错误。

## 结论

- 代码层面的 Android 与 API 联通能力已落地，并通过 API 与 Android 编译校验；
- 若要完成设备级回归，需要先连接可用 Android 模拟器后重跑 `connectedDebugAndroidTest`。
