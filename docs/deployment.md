# moco-admin 部署文档

## 1. 部署概览

`moco-admin` 支持以下部署方式：

- 本地开发部署
- 单机部署
- Nginx + 前后端分离部署
- Docker 辅助依赖部署

推荐生产拓扑：

1. Nginx 提供前端静态资源与反向代理
2. `moco-admin.jar` 独立运行
3. MySQL 独立部署
4. Redis 独立部署

## 2. 环境要求

### 后端环境

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Redis 7.x

### 前端环境

- Node.js 18+
- npm 9+

## 3. 端口规划建议

默认建议：

- 前端开发端口：`18081`
- 后端端口：`8080`
- MySQL：`3306`
- Redis：`6379`

## 4. 本地依赖启动

项目根目录已经提供 `docker-compose.yml`，可直接启动 MySQL 和 Redis：

```bash
docker compose up -d
```

默认会创建：

- 数据库：`moco_admin`
- MySQL 用户：`root`
- MySQL 密码：`password`

并导入：

- `sql/moco_20260320.sql`
- `sql/quartz.sql`

## 5. 数据库初始化

如果不使用 Docker 初始化，也可以手动导入 SQL：

```sql
CREATE DATABASE moco_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

然后导入：

```bash
mysql -uroot -p moco_admin < sql/moco_20260320.sql
mysql -uroot -p moco_admin < sql/quartz.sql
```

注意事项：

- 数据库必须使用 `utf8mb4`
- 如果初始化后页面字典仍异常，检查 Redis 缓存是否需要清理

## 6. 后端构建与运行

### 6.1 构建

```bash
mvn clean package -DskipTests
```

生成产物：

```text
moco-admin/target/moco-admin.jar
```

### 6.2 直接运行

```bash
java -jar moco-admin/target/moco-admin.jar
```

### 6.3 使用脚本运行

Linux / macOS：

```bash
chmod +x moco.sh
./moco.sh start
./moco.sh status
./moco.sh logs
./moco.sh stop
```

Windows：

```bat
moco.bat start
moco.bat status
moco.bat logs
moco.bat stop
```

脚本支持：

- 自动定位 `moco-admin/target/moco-admin.jar`
- 自动创建 `logs/`、`runtime/`
- 支持通过 `JAVA_HOME` 指定 Java 路径
- 支持通过 `JVM_OPTS` 和 `APP_ARGS` 自定义参数

## 7. 前端构建与运行

### 7.1 开发模式

```bash
cd moco-ui
npm install
npm run dev -- --port 18081
```

### 7.2 生产构建

```bash
cd moco-ui
npm run build:prod
```

构建产物位于：

```text
moco-ui/dist
```

## 8. Nginx 部署示例

将 `moco-ui/dist` 上传到站点目录，例如 `/data/www/moco-ui`。

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /data/www/moco-ui;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

仓库已提供可直接参考的站点配置文件：

- `deploy/nginx.moco-admin.conf`

## 9. systemd 服务示例

Linux 生产环境建议使用 `systemd` 托管后端服务。仓库已提供示例文件：

- `deploy/moco-admin.service`

使用方式示例：

```bash
sudo cp deploy/moco-admin.service /etc/systemd/system/moco-admin.service
sudo systemctl daemon-reload
sudo systemctl enable moco-admin
sudo systemctl start moco-admin
sudo systemctl status moco-admin
```

上线前请先按实际环境修改：

- `User` / `Group`
- `WorkingDirectory`
- `JAVA_HOME`
- `ExecStart` / `ExecStop` 路径

## 10. 关键配置文件

- `moco-admin/src/main/resources/application.yml`
- `moco-admin/src/main/resources/application-druid.yml`
- `moco-admin/src/main/resources/logback.xml`
- `docker-compose.yml`

重点关注：

- 服务端口
- MySQL / Redis 地址
- 上传目录
- 日志目录
- 验证码、注册开关

## 11. 生产建议

- 固定使用 JDK 17
- 生产环境修改默认账号密码
- Redis 开启持久化
- 日志目录独立挂载并归档
- 使用 Nginx 做统一入口和 HTTPS
- 使用 `systemd` 托管后端服务
- 将公开接口和文档能力按环境开关收紧

## 12. 常见问题排查

### 12.1 页面出现乱码

排查顺序：

1. 检查 MySQL 库、表、连接是否为 `utf8mb4`
2. 检查 SQL 是否按 UTF-8 正确导入
3. 检查 Redis 字典缓存是否仍是旧值

清理 Redis 字典缓存示例：

```bash
docker exec moco-redis sh -lc "redis-cli --raw KEYS 'sys_dict:*' | xargs -r redis-cli DEL"
```

### 12.2 后端无法连接 MySQL

检查：

- MySQL 是否启动
- `application-druid.yml` 中的连接参数是否正确
- JDK 与驱动是否兼容

### 12.3 前端接口 404

检查：

- 前端代理配置是否正确
- Nginx 是否反向代理 `/prod-api`
- 后端是否正常启动

## 13. 默认访问信息

- 后端接口：`http://localhost:8080`
- Swagger：`http://localhost:8080/swagger-ui/index.html`
- 默认账号：`admin`
- 默认密码：`admin123`
