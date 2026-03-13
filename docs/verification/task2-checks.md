# Task 2 Verification Checks

日期：2026-03-13

## 1) 失败阶段（先失败）

为保证“实现已存在时仍可复核失败”，先临时重命名 `services/api/src/server.ts`，再运行测试。

命令：

```powershell
cd services/api
Rename-Item "src/server.ts" "server.ts.bak" -Force
npm test -- health.test.ts
Rename-Item "src/server.ts.bak" "server.ts" -Force
```

输出摘要：
- `FAIL test/health.test.ts`
- `TS2307: Cannot find module '../src/server'`
- `Test Suites: 1 failed, 1 total`

说明：
- 该失败由临时移除 `server.ts` 导致，符合“先失败”可复核要求。
- 测试后已将文件名恢复为 `server.ts`，最终实现文件保持不变。

## 2) 通过阶段（再通过）

命令：

```powershell
cd services/api
npm test -- health.test.ts
```

输出摘要：
- `PASS test/health.test.ts`
- `GET /health returns ok`
- `Test Suites: 1 passed, 1 total`
- `Tests: 1 passed, 1 total`

## 3) 可选构建验证

命令：

```powershell
cd services/api
npm run build
```

输出摘要：
- 执行 `tsc -p tsconfig.build.json`
- 退出码 `0`（构建成功）
