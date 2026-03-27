# moco-admin

`moco-admin` 是基于 [`RuoYi-Vue`](https://github.com/yangzongzhuan/RuoYi-Vue) 主线版本迁移并品牌化改造的前后端分离后台系统，当前已统一为 `moco` 命名体系，并完成首页、Logo、脚本、初始化数据与默认配置的整合。

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.x
- Redis 7.x

## 快速开始

### 1. 启动依赖

```bash
docker compose up -d
```

### 2. 构建后端

```bash
mvn clean package -DskipTests
```

### 3. 启动后端

Linux / macOS:

```bash
chmod +x moco.sh
./moco.sh start
```

Windows:

```bat
moco.bat start
```

### 4. 启动前端

```bash
cd moco-ui
npm install
npm run dev -- --port 18081
```

## 默认信息

- 后端地址：`http://localhost:8080`
- Swagger：`http://localhost:8080/swagger-ui/index.html`
- 前端开发地址：`http://localhost:18081`
- 默认账号：`admin`
- 默认密码：`admin123`

## 文档导航

- 架构文档：[docs/architecture.md](/Users/makun/llm_project/moco-admin/docs/architecture.md)
- 部署文档：[docs/deployment.md](/Users/makun/llm_project/moco-admin/docs/deployment.md)

## 关键目录

- `moco-admin`：Spring Boot 启动模块
- `moco-framework`：认证、安全、配置、拦截器
- `moco-system`：系统核心业务
- `moco-quartz`：定时任务
- `moco-generator`：代码生成
- `moco-common`：通用能力
- `moco-ui`：前端管理端
- `sql`：数据库脚本
- `docs`：项目文档

## 许可证与来源

本项目基于上游 `RuoYi-Vue` 改造，保留原项目的 MIT License，详见根目录 [`LICENSE`](/Users/makun/llm_project/moco-admin/LICENSE)。
