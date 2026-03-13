# Task 3 校验记录（先失败后通过）

日期：2026-03-13  
范围：`services/api` 的 `auth + health` 相关测试

## 1) RED：先让测试失败（新增审查用例后）

命令：

```bash
npm test -- test/auth.register-login.test.ts test/health.test.ts
```

输出摘要：

- `PASS test/health.test.ts`
- `FAIL test/auth.register-login.test.ts`
- 失败点 1：`JsonWebTokenError: invalid signature`
  - 位置：`POST /auth/register returns token with userId` 中 `jwt.verify(...)`
- 失败点 2：`Expected: 400, Received: 201`
  - 位置：`returns 400 for missing or empty fields`

结论：新增安全与校验要求在旧实现下未满足，RED 成立。

## 2) GREEN：最小修复后测试通过

修复项（最小改动）：

- `auth.service.ts`
  - 移除 `dev-secret` 回退；无 `JWT_SECRET` 时拒绝签发 token
  - JWT 增加 `expiresIn`、`issuer`、`audience`
- `auth.routes.ts`
  - 增强输入校验：缺字段、空字符串、邮箱格式、密码最短长度（>=8）
- `auth.register-login.test.ts`
  - 增补：重复注册 409、错误密码 401、缺字段/空字符串 400、token verify + `exp`

命令：

```bash
npm test -- test/auth.register-login.test.ts test/health.test.ts
```

输出摘要：

- `PASS test/health.test.ts`
- `PASS test/auth.register-login.test.ts`
- `Test Suites: 2 passed, 2 total`
- `Tests: 6 passed, 6 total`

结论：GREEN 成立，且 `health` 未受破坏。
