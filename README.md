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

### 启动 API（本地）

在 `services/api` 目录执行：

- 安装依赖：`npm install`
- 启动开发服务（需提供 JWT 密钥）：
  - PowerShell: `$env:JWT_SECRET="dev_only_secret_for_local_run"; npm run dev`
- 默认监听：`http://localhost:3000`

### 启动 Android（连接本地 API）

- 确认模拟器已启动（API 34）。
- 在 `apps/android` 目录执行：`.\\gradlew.bat installDebug`
- Android 默认访问 `http://10.0.2.2:3000` 作为 API 地址。

## 安全说明（开发环境）

- `docker-compose.yml` 默认从环境变量读取凭据，不在文件中写死密码
- Redis 通过 `--requirepass` 启用鉴权
- 端口映射仅用于本地开发，生产环境需改为内网网络策略与密钥管理

## 当前状态

当前仓库已完成 Task 1-11，并完成 Android -> API 基础联通：
- 登录：优先走 `/auth/login`，首次用户自动注册兜底；
- 发帖：优先走 `/posts`，同时保留本地离线体验；
- 发现页：登录后自动尝试同步 `/feed/recommended`；
- 无后端时可退化到离线演示模式，保证核心流程可体验。

近期可用性增强（迭代）：
- 发现页支持关键词筛选（标题/作者/参数）；
- 增加“每周挑战”入口与挑战引导页；
- 增加作品举报入口，并接入 `/reports` 提交。
- 账号邮箱、挑战参与状态、举报历史支持本地持久化并在个人页展示。
- 每周挑战参与已接入 API：`POST /challenges/weekly/join`。
- 新增关注关系与关注流 API：`POST /social/follow`、`GET /feed/following`。
- Android 发现页支持“推荐流/关注流”切换，并支持关注作者动作。
- 举报历史已后端化：`GET /reports?reporterEmail=...`，客户端提交举报时会携带举报人邮箱并同步历史。
- 推荐流新增结构化筛选参数：`genre`、`gearBrand`、`city`、`challengeTag`。
