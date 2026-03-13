# Photo Social App Monorepo

面向进阶摄影爱好者的社交应用仓库基线，当前包含系统文档与本地开发基础设施定义。

## 仓库结构（初始化）

- `apps/android`: Android 客户端（Jetpack Compose，后续任务创建）
- `services/api`: 后端 API 服务（Node.js + Fastify，后续任务创建）
- `services/recommendation`: 推荐与排序服务（后续任务创建）
- `packages/shared`: 跨端共享类型与接口约束（后续任务创建）
- `docs/architecture`: 架构说明文档
- `docs/verification`: 任务验证记录

## 服务边界（MVP）

- API 服务：用户鉴权、作品发布、点评、举报、发现流接口
- Recommendation 服务：对作品进行质量/新鲜度打分并提供排序能力
- PostgreSQL：核心业务数据存储
- Redis：缓存、会话与短期状态
- MinIO：S3 兼容对象存储，用于图片与媒体资源

## 本地开发启动

1. 复制环境变量模板：
   - `cp .env.example .env`（Windows PowerShell 可用 `Copy-Item .env.example .env`）
2. 启动基础依赖服务：
   - `docker compose up -d`
3. 查看容器状态：
   - `docker compose ps`
4. 关闭并清理容器：
   - `docker compose down`

## 安全说明（开发环境）

- `docker-compose.yml` 默认从环境变量读取凭据，不在文件中写死密码
- Redis 通过 `--requirepass` 启用鉴权
- 端口映射仅用于本地开发，生产环境需改为内网网络策略与密钥管理

## 当前状态

当前仓库已完成 Task 1-7：开发基线、API 健康检查、鉴权、帖子与 EXIF、点评系统、推荐发现流、审核与举报流程；后续继续推进 Task 8-11。
