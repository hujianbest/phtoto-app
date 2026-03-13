# Task 20 校验记录（举报历史后端化与客户端同步）

日期：2026-03-13

## 目标

- 提供举报历史查询 API（按举报人邮箱过滤）；
- Android 举报提交时携带 `reporterEmail`；
- 个人页举报历史由“本地记录”升级为“服务端优先同步 + 本地兜底”。

## TDD 记录

### RED

新增测试：`services/api/test/reports.history.test.ts`  
执行：

```powershell
npm test -- reports.history.test.ts
```

结果：
- 失败（预期 RED）：`GET /reports` 返回 `404`。

### GREEN

最小实现：
- `services/api/src/modules/reports/report.entity.ts`
  - 新增 `reporterEmail` 字段与 `listByReporterEmail`。
- `services/api/src/modules/reports/report.routes.ts`
  - 新增 `GET /reports?reporterEmail=...`；
  - `POST /reports` 支持可选 `reporterEmail` 校验与入库。
- Android：
  - `ApiClient.createPostReport` 支持 `reporterEmail`；
  - 新增 `ApiClient.fetchReportHistory(email)`；
  - `AuthViewModel.syncReportHistoryFromRemote()`；
  - 举报成功后触发服务端历史同步。

再次执行：

```powershell
npm test -- reports.history.test.ts
```

结果：
- 通过（GREEN）。

## 回归验证

### API

```powershell
npm run build
npm test -- reports.history.test.ts moderation.report-flow.test.ts auth.register-login.test.ts
```

结果：
- 构建通过；
- 3 个测试套件、8 个测试全部通过。

### Android

```powershell
.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin
.\gradlew.bat connectedDebugAndroidTest
```

结果：
- 编译通过；
- 设备级回归通过（6 tests，`BUILD SUCCESSFUL`）。

## 结论

- 举报历史已具备服务端承载与查询能力；
- Android 端在提交举报后可服务端优先同步个人历史，离线时仍有本地记录兜底。
