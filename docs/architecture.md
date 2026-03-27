# moco-admin 项目架构文档

## 1. 项目定位

`moco-admin` 是一个基于 `RuoYi-Vue` 主线改造而来的前后端分离后台管理系统，保留权限认证、组织架构、定时任务、代码生成、系统监控、缓存监控等核心能力，并统一完成 `moco` 品牌重命名。

目标人群：

- 个人管理后台
- 中小型业务管理平台
- 内部运营系统
- 快速搭建后台脚手架的二次开发项目

## 2. 总体架构

系统采用前后端分离架构：

- 前端：`Vue 2 + Vuex + Vue Router + Element UI`
- 后端：`Spring Boot 4 + Spring Security + JWT + MyBatis`
- 数据层：`MySQL 8`
- 缓存层：`Redis 7`

逻辑关系如下：

1. 浏览器访问 `moco-ui`
2. 前端通过 `/dev-api` 或生产反向代理访问后端 REST 接口
3. 后端通过 Spring Security + JWT 进行身份认证与权限校验
4. MyBatis 访问 MySQL 持久化业务数据
5. Redis 提供字典缓存、登录会话、重复提交控制、限流等能力

## 3. 目录结构

项目根目录如下：

```text
moco-admin/
├── moco-admin/        # Spring Boot 启动模块
├── moco-common/       # 通用工具、基础模型、常量、异常
├── moco-framework/    # 安全、配置、拦截器、序列化、Web层通用能力
├── moco-system/       # 系统业务模块：用户、角色、菜单、公告、字典、参数等
├── moco-quartz/       # 定时任务模块
├── moco-generator/    # 代码生成模块
├── moco-ui/           # Vue 前端项目
├── sql/               # 初始化 SQL
├── docs/              # 项目文档
├── docker-compose.yml # 本地 MySQL / Redis 编排
├── moco.sh            # Linux / macOS 启动脚本
└── moco.bat           # Windows 启动脚本
```

## 4. 后端模块说明

### 4.1 `moco-admin`

启动入口模块，负责：

- 读取 `application.yml`、`application-druid.yml`
- 装配 Spring Boot Web 容器
- 加载安全、MyBatis、Redis、定时任务等配置

关键入口：

- `com.moco.MocoApplication`

### 4.2 `moco-common`

公共基础模块，主要包含：

- 常量定义
- 基础实体
- DTO / VO / AjaxResult
- 工具类
- Redis 工具封装
- 注解与枚举

这个模块被其他后端模块广泛依赖。

### 4.3 `moco-framework`

框架层模块，负责系统基础设施：

- Spring Security 配置
- JWT 过滤器
- Druid 配置
- MyBatis 配置
- Redis 配置
- XSS 过滤
- 防重复提交拦截
- 全局异常处理

### 4.4 `moco-system`

业务核心模块，主要包含：

- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- 岗位管理
- 字典管理
- 参数管理
- 通知公告
- 在线用户
- 操作日志 / 登录日志
- 缓存管理

这是日常二次开发最常改动的模块。

### 4.5 `moco-quartz`

定时任务模块，负责：

- 任务定义
- 任务执行
- 任务日志
- 调度开关与状态控制

### 4.6 `moco-generator`

代码生成模块，负责：

- 读取业务表结构
- 生成后端代码
- 生成前端页面
- 生成树表、主子表等模板代码

## 5. 前端架构说明

前端位于 `moco-ui`，主要结构如下：

```text
moco-ui/src
├── api/         # 接口封装
├── assets/      # 图片、样式、图标、logo
├── components/  # 通用组件
├── layout/      # 布局系统
├── router/      # 路由定义
├── store/       # Vuex 状态管理
├── utils/       # 请求、鉴权、字典、通用方法
└── views/       # 业务页面
```

前端核心特点：

- 动态路由按权限加载
- 字典数据统一通过 `dict` 机制渲染
- 布局、顶部栏、标签页、侧边栏解耦
- 页面级接口按模块划分到 `src/api`

## 6. 认证与权限模型

系统采用：

- 登录认证：用户名 + 密码 + 验证码
- Token 模型：JWT
- 权限控制：角色 + 权限字符
- 菜单控制：菜单树 + 按钮级权限

认证流程：

1. 用户登录后，后端签发 JWT
2. 前端将 JWT 保存到本地
3. 后续请求通过请求头携带 Token
4. 后端过滤器解析 Token 并恢复登录上下文
5. 权限注解和按钮权限字符共同生效

## 7. 数据与缓存设计

### 7.1 MySQL

主要业务表包括：

- `sys_user`
- `sys_role`
- `sys_menu`
- `sys_dept`
- `sys_post`
- `sys_dict_type`
- `sys_dict_data`
- `sys_notice`
- `sys_oper_log`
- `sys_logininfor`
- `sys_job`
- `sys_job_log`

### 7.2 Redis

Redis 主要用于：

- 登录态和 Token 相关缓存
- 字典缓存 `sys_dict:*`
- 防重复提交缓存
- 限流相关缓存
- 其他临时业务缓存

注意：

- 当数据库字典数据修复或初始化后，如果页面仍显示旧值，优先检查 Redis 字典缓存是否需要清理。

## 8. 请求处理链路

一次典型请求的链路如下：

1. 浏览器发起接口请求
2. Vue 请求封装器统一处理 Token、异常、下载等逻辑
3. Spring MVC 接收请求
4. Spring Security 进行认证和授权
5. Controller 调用 Service
6. Service 调用 Mapper
7. Mapper 通过 MyBatis 访问数据库
8. 返回统一响应体给前端

## 9. 当前项目定制点

相较于原始 `RuoYi-Vue`，当前项目已经完成这些关键调整：

- 品牌统一替换为 `moco`
- Java 包名统一改为 `com.moco`
- 启动脚本改为 `moco.sh`、`moco.bat`
- 首页已重做为工作台样式
- Logo、favicon、默认标题统一为 `moco`
- 本地依赖通过 `docker-compose.yml` 快速启动

## 10. 二次开发建议

建议按照下面的分层原则继续扩展：

- 公共工具放在 `moco-common`
- 框架能力放在 `moco-framework`
- 业务能力放在 `moco-system`
- 新业务页面放在 `moco-ui/src/views/<业务域>`
- 新接口放在 `moco-ui/src/api/<业务域>.js`

如果新增独立业务域较大，也可以参考 `moco-system` 的模式继续拆分新的后端模块。
