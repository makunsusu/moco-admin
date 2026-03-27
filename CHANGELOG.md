# Changelog

## v1.0.0 - 2026-03-28

首个可用版本，完成 `moco-admin` 的基础落地、品牌迁移和运行验证。

### 新增

- 基于 `RuoYi-Vue` 主线完成前后端分离项目迁移
- 完整迁移后端多模块结构：`moco-admin`、`moco-common`、`moco-framework`、`moco-system`、`moco-quartz`、`moco-generator`
- 完整迁移前端管理端 `moco-ui`
- 新增 `理财大师 -> 交易市场管理` 模块
- 新增交易市场维护能力，内置中国主要交易市场初始化数据
- 新增 `docker-compose.yml`，用于本地快速启动 MySQL 和 Redis
- 新增项目架构文档与部署文档

### 调整

- 全量将项目品牌标识从 `若依 / ruoyi / ry` 迁移为 `moco`
- Java 包名统一改为 `com.moco`
- Maven 模块与默认配置统一改为 `moco-*`
- 首页重做为工作台样式
- 登录页、Logo、favicon、标题、脚本入口统一改为 `moco`
- 启动脚本统一为 `moco.sh`、`moco.bat`
- README 重写为面向仓库首页的项目说明

### 修复

- 修复数据库初始化与导入时的中文乱码问题
- 修复 Redis 字典缓存导致的字典状态显示乱码问题
- 修复 `moco.sh` 在旧 Java 环境下误启动失败的问题
- 修复新菜单图标缺失问题

### 验证

- 后端 `mvn package -DskipTests` 通过
- 前端 `npm run build:prod` 通过
- MySQL / Redis 联通验证通过
- 后端接口 `http://localhost:8080/captchaImage` 验证通过
