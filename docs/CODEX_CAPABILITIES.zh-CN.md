# Codex 能力中文速查

更新时间：2026-03-29

## 你的当前环境

- 全局 `agent`：`3`
- 已安装 `skill`：`135`
- 已启用 `plugin`：`1`
- 已配置 `MCP server`：`6`
- 已安装 `ECC command prompts`：`68`

## 一句话理解这些东西

- `agent`：专家角色，适合“换一种工作模式”
- `skill`：专项能力，适合“这类任务按某套方法做”
- `plugin`：外部系统扩展，适合“连 GitHub 之类外部能力”
- `MCP`：底层工具通道，适合“查文档、搜网页、开浏览器、存记忆”
- `command`：工作流入口，适合“快速启动一套固定流程”
- `ECC command`：你现在在 Codex 里主要表现为 `ecc-*` prompt

## 在 Codex 里怎么用

### 1. 用 agent

直接在请求里点名。

示例：

- `Use the explorer agent to trace the login flow.`
- `Use the reviewer agent on the current diff.`
- `Use docs_researcher before changing this API integration.`

### 2. 用 skill

也是直接在请求里点名。

示例：

- `Use tdd-workflow to fix this bug.`
- `Use security-review on this auth flow.`
- `Use documentation-lookup before editing this Next.js code.`

### 3. 用 ECC command

你当前安装的 ECC command 在 Codex 里主要是 `ecc-*` prompts。

示例：

- `Use ecc-plan for this feature.`
- `Use ecc-code-review on the current branch.`
- `Use ecc-verify before merge.`

### 4. 用 plugin / MCP

一般不需要记工具名，直接说目标即可。

示例：

- `Summarize PR #123.` -> GitHub plugin / github MCP
- `Look up the latest official React docs.` -> `context7`
- `Search the web for recent release notes.` -> `exa`
- `Open the page and test the form flow.` -> `playwright`

## 你当前最值得常用的能力

### 高频 agent

- `explorer`
  用途：先摸清代码路径、调用链、入口文件
  适合：改动前、排查前、读陌生项目时

- `reviewer`
  用途：做偏严格的代码审查
  适合：改完代码后、准备合并前、检查回归风险

- `docs_researcher`
  用途：核对官方文档和最新 API 行为
  适合：框架升级、SDK 改动、你不确定文档是否变了的时候

### 高频 skill

- `tdd-workflow`
  用途：测试驱动开发、修 bug、补回归测试

- `verification-loop`
  用途：在交付前做完整验证

- `security-review`
  用途：鉴权、输入校验、上传、支付、密钥、接口安全

- `documentation-lookup`
  用途：查官方文档，避免靠旧知识猜

- `search-first`
  用途：先查现成方案、库、最佳实践，再决定要不要自己写

- `coding-standards`
  用途：TS/JS/React/Node 的通用规范

- `frontend-patterns`
  用途：React/Next.js 前端实现

- `backend-patterns`
  用途：后端接口、服务层、架构实现

- `e2e-testing`
  用途：页面流程和关键路径测试

- `design-system`
  用途：样式统一、组件一致性、设计规范审查

- `repo-scan`
  用途：快速盘点陌生仓库

- `codebase-onboarding`
  用途：快速理解新项目结构和入口

- `database-migrations`
  用途：做表结构变更、数据迁移、上线策略

- `benchmark`
  用途：做性能前后对比

### 高频 ECC command

- `ecc-plan`
  用途：把需求拆成可执行步骤

- `ecc-code-review`
  用途：快速走一遍代码审查流程

- `ecc-verify`
  用途：合并前/交付前做总验证

- `ecc-build-fix`
  用途：修构建错误、类型错误、编译错误

- `ecc-context-budget`
  用途：检查上下文是否太臃肿

- `ecc-save-session`
  用途：保存当前阶段上下文

- `ecc-resume-session`
  用途：恢复之前的上下文

- `ecc-rules-pack-common`
  用途：给当前任务套上 ECC 通用规则

- `ecc-rules-pack-typescript`
  用途：当前任务按 TypeScript 规则来做

- `ecc-tool-run-tests`
  用途：统一跑测试并整理结果

### 高频 plugin / MCP

- `github@openai-curated`
  用途：PR、Issue、review、分支、提交

- `context7`
  用途：官方文档查询

- `exa`
  用途：网页搜索、代码搜索、信息检索

- `playwright`
  用途：浏览器自动化、页面验证

- `memory`
  用途：存项目约定、长期记忆

## 遇到什么问题先用哪个

### 想先看懂代码，不急着改

优先：

- `explorer`
- `codebase-onboarding`
- `repo-scan`

示例：

- `Use the explorer agent to trace the order creation flow first.`

### 要修一个 bug

优先：

- `tdd-workflow`
- `ecc-build-fix`
- `reviewer`

示例：

- `Use tdd-workflow to fix this bug, then run ecc-code-review.`

### 要做一个新功能

优先：

- `ecc-plan`
- `tdd-workflow`
- `verification-loop`

示例：

- `Use ecc-plan for this feature, then implement with tdd-workflow.`

### 要查官方文档/API 变化

优先：

- `docs_researcher`
- `documentation-lookup`
- `context7`

示例：

- `Use docs_researcher and documentation-lookup before modifying this SDK integration.`

### 要做安全敏感改动

优先：

- `security-review`
- `reviewer`
- `ecc-verify`

场景：

- 登录注册
- 权限控制
- 用户输入
- 文件上传
- 支付
- 密钥和 token

### 要做前端页面/UI

优先：

- `frontend-patterns`
- `design-system`
- `e2e-testing`
- `playwright`

### 要做后端/API

优先：

- `backend-patterns`
- `api-design`
- `security-review`
- `database-migrations`

### 要审查代码或 PR

优先：

- `reviewer`
- `ecc-code-review`
- `verification-loop`

示例：

- `Use the reviewer agent and ecc-code-review on the current diff.`

### 要在 GitHub 上做事

优先：

- `github@openai-curated`

适合：

- 看 PR 摘要
- 查 review comments
- 开 issue
- 看分支和提交

### 要搜网页 / 最新资料

优先：

- `exa`
- `documentation-lookup`
- `docs_researcher`

### 要验证页面流程

优先：

- `playwright`
- `e2e-testing`
- `browser-qa`

### 会话太长，怕上下文乱了

优先：

- `ecc-context-budget`
- `strategic-compact`
- `ecc-save-session`
- `ecc-resume-session`

## 我建议你平时的默认工作流

### 代码修改型任务

推荐顺序：

1. `explorer` 或 `ecc-plan`
2. `documentation-lookup`，如果涉及框架/库
3. `tdd-workflow`
4. `reviewer`
5. `ecc-verify`

适合：

- 修 bug
- 做新功能
- 重构

### 前端任务

推荐顺序：

1. `explorer`
2. `frontend-patterns`
3. `design-system`
4. `e2e-testing`
5. `playwright`
6. `ecc-code-review`

### 后端/API 任务

推荐顺序：

1. `explorer`
2. `backend-patterns`
3. `api-design`
4. `security-review`
5. `database-migrations`
6. `ecc-verify`

### 陌生项目接手

推荐顺序：

1. `repo-scan`
2. `codebase-onboarding`
3. `explorer`
4. `documentation-lookup`

## 你当前的 agent

### `explorer`

用途：

- 只读探索代码
- 找入口、调用链、依赖关系

怎么用：

- `Use the explorer agent to map the payment flow.`

### `reviewer`

用途：

- 做偏严格的代码审查
- 重点看 bug、回归、安全、缺失测试

怎么用：

- `Use the reviewer agent on the current branch.`

### `docs_researcher`

用途：

- 查官方文档、核对 API 行为

怎么用：

- `Use docs_researcher before changing this framework integration.`

## 你当前的 plugin

### `github@openai-curated`

用途：

- GitHub 仓库、PR、Issue、review、分支、提交相关操作

怎么用：

- `Summarize PR #45.`
- `List unresolved review comments on this PR.`
- `Create an issue for this bug.`

## 你当前的 MCP

### `github`

用途：

- GitHub API 相关操作

### `context7`

用途：

- 查官方文档

### `exa`

用途：

- 搜网页、搜代码、做研究

### `memory`

用途：

- 保存长期记忆、项目约定

### `playwright`

用途：

- 浏览器自动化、页面测试

### `sequential-thinking`

用途：

- 多步推理、复杂问题拆解

## 你当前最值得优先记住的组合

### 通用开发组合

- `explorer` + `tdd-workflow` + `reviewer` + `ecc-verify`

### 前端组合

- `frontend-patterns` + `design-system` + `e2e-testing` + `playwright`

### 后端组合

- `backend-patterns` + `api-design` + `security-review` + `database-migrations`

### 文档和调研组合

- `docs_researcher` + `documentation-lookup` + `exa`

### 陌生项目组合

- `repo-scan` + `codebase-onboarding` + `explorer`

## 其他说明

- 完整英文说明文档在：
  [CODEX_CAPABILITIES.md](/Users/makun/llm_project/moco-admin/docs/CODEX_CAPABILITIES.md)
- 这份中文文档偏“高频使用”和“速查”
- 如果你后面想要，我还可以继续补一版：
  `按前端 / 后端 / Python / Java / Go / 产品文档 分场景推荐 skill 清单`

