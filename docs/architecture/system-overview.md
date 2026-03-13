# 系统概览

## 目标

构建一个 Android 首发的摄影社区应用，支持作品发布、EXIF 信息展示、结构化点评、发现流与基础风控流程。

## 架构分层

- 客户端：`apps/android`（Jetpack Compose）
- API 层：`services/api`（Fastify）
- 推荐层：`services/recommendation`（排序与特征计算）
- 基础设施：PostgreSQL、Redis、MinIO

## 核心数据流

1. 用户在 Android 端登录并上传作品。
2. API 处理业务逻辑；当前 MVP 阶段帖子接口接收 `title`/`description`/`imageUrl`/`intent`，并以 `imageUrl` 引用媒体资源，部分模块使用内存存储验证链路，后续补齐 MinIO 上传与 PostgreSQL 持久化。
3. 推荐服务读取候选内容并计算排序分数。
4. API 聚合推荐结果后返回发现流。

## 非功能基线

- 通过 `docker-compose.yml` 提供本地统一依赖环境。
- 使用 `.editorconfig` 统一缩进与换行风格。
- 使用 `.gitignore` 避免提交构建产物、日志与敏感文件。
