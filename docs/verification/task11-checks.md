# Task 11 Checks - 可观测性、灰度与上线清单

日期：2026-03-13  
范围：`/metrics` SLI 最小实现、server 接入、health+metrics 回归、上线与监控文档

## TDD 记录

### RED（先写测试，再执行）
- 新增测试：`services/api/test/metrics.health-sli.test.ts`
- 目标行为：`GET /metrics` 返回文本，并包含 `availability`、`latency`、`error_rate`
- 执行命令：
  - `npm test -- metrics.health-sli.test.ts`
- 实际结果：**失败（符合 RED 预期）**
  - 关键信息：`Expected: 200`，`Received: 404`

### GREEN（补最小实现后再执行）
- 最小实现：
  - 新增 `services/api/src/plugins/metrics.ts`
  - 在 `services/api/src/server.ts` 接入 `registerMetricsPlugin(app)`
- 执行命令：
  - `npm test -- metrics.health-sli.test.ts`
- 实际结果：**通过（GREEN）**
  - `PASS test/metrics.health-sli.test.ts`（3 tests）

## API 回归（至少 health + metrics）

- 执行命令：
  - `npm test -- health.test.ts metrics.health-sli.test.ts`
- 实际结果：**通过**
  - `PASS test/metrics.health-sli.test.ts`
  - `PASS test/health.test.ts`
  - 汇总：`Test Suites: 2 passed, 2 total`，`Tests: 4 passed, 4 total`

## 文档产出

- `docs/release/mvp-go-live-checklist.md`
  - 包含灰度步骤、回滚策略、发布前检查项
- `docs/ops/monitoring-dashboard-spec.md`
  - 包含指标定义、采样粒度、告警阈值
