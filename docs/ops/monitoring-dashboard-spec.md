# 监控看板规格（Task 11）

## 指标定义

- `availability`：可用性比例，范围 `[0, 1]`。定义为 `1 - 错误请求数 / 总请求数`（按时间窗口聚合）。
- `latency`：请求延迟（毫秒），建议展示 `p50/p95/p99`，MVP 最少展示 `p95`。
- `error_rate`：错误率，范围 `[0, 1]`。定义为 `5xx 请求数 / 总请求数`。

## 采样与展示粒度

- 采集间隔：15 秒（MVP 可接受 30 秒）。
- 聚合窗口：1 分钟滚动窗口。
- 看板时间范围：
  - 默认：最近 30 分钟
  - 可选：最近 6 小时、24 小时
- 图表建议：
  - 折线图：`availability`、`error_rate`
  - 折线或分位图：`latency (p95)`

## 告警阈值（口径统一）

- 可用性告警：`availability < 0.995`（即 99.5%）持续 5 分钟（Warning）；`< 0.99`（即 99%）持续 5 分钟（Critical）。
- 延迟告警：`p95 latency > 500ms` 持续 10 分钟（Warning）；`> 1000ms` 持续 10 分钟（Critical）。
- 错误率告警：`error_rate > 0.01`（即 1%）持续 5 分钟（Warning）；`> 0.02`（即 2%）持续 5 分钟（Critical）。

## 数据来源与接口

- API 暴露端点：`GET /metrics`
- 返回类型：`text/plain; charset=utf-8`
- 当前最小实现包含关键指标名：`availability`、`latency`、`error_rate`
