# Codex Environment Reference

Last updated: 2026-03-29

## Snapshot

- Global agents: `3`
- Installed skills: `135`
- Enabled plugins: `1`
- Configured MCP servers: `6`
- ECC command prompts: `68`
- ECC local command sources: `60`
- ECC local rule files: `39`

## What You Have

Your current Codex setup is a mix of:

- Native/global Codex config in `~/.codex/config.toml`
- Three reusable global agents in `~/.codex/agents/`
- A large shared skill library in `~/.codex/skills/`
- One enabled plugin: `github@openai-curated`
- Six MCP servers wired into Codex
- ECC installed as global AGENTS instructions plus prompt/rule assets in `~/.codex`

## How To Use These In Codex

### Commands

There are two different things worth calling "commands" in your current setup.

#### 1. Native Codex CLI commands

These are built into Codex itself.

Examples:

- `codex exec`
- `codex review`
- `codex resume`
- `codex fork`
- `codex mcp`

Use these in your terminal when you want to operate Codex itself.

#### 2. ECC command prompts

ECC originally defines many workflows as commands. In your Codex setup, those workflows are represented mainly as reusable prompts in `~/.codex/prompts/`.

Examples:

- `ecc-plan`
- `ecc-code-review`
- `ecc-verify`
- `ecc-build-fix`

Use these in Codex by referencing the prompt name in your request.

Example:

- `Use ecc-code-review on the current diff.`

### Skills

Skills are usually best invoked by naming them in plain language inside your request.

Examples:

- `Use the tdd-workflow skill to fix the failing test and keep coverage intact.`
- `Use documentation-lookup before changing this Next.js API route.`
- `Use security-review on this auth flow.`

Practical rule:

- If a skill is clearly relevant, just mention its name.
- If you do not mention one, Codex may still use it automatically when the trigger matches.

### Agents

Your current global agents are reusable specialist roles. In Codex, ask for them explicitly when you want a certain style of work.

Examples:

- `Use the reviewer agent on this branch.`
- `Use the explorer agent to map the auth flow before editing anything.`
- `Use docs_researcher to verify the latest API behavior first.`

### Plugins

Plugins extend Codex with integrated capabilities. You currently have the GitHub plugin enabled, so natural-language requests like these work well:

- `Summarize this PR.`
- `Check unresolved review comments.`
- `Create an issue for this bug.`
- `Review the changed files in PR #123.`

### MCP Servers

MCP servers are the actual tool backends. You usually do not call them by config name; you ask for the outcome.

Examples:

- `Look up the latest React docs.` -> `context7`
- `Search the web for the latest release note.` -> `exa`
- `Open the page and click the submit button.` -> `playwright`
- `Remember this project convention.` -> `memory`

### ECC Prompts

ECC command prompts are stored in `~/.codex/prompts/` as reusable workflow templates. Depending on your Codex version, they may appear in prompt tools or prompt pickers; the safest usage is simply to reference the prompt name in your request.

Examples:

- `Use ecc-verify on this repo before we merge.`
- `Use ecc-plan to break this feature into steps.`
- `Use ecc-code-review on the current diff.`
- `Apply ecc-rules-pack-typescript for this task.`

## Global Agents

### `explorer`

Purpose:
- Read-only codebase exploration.
- Best for tracing execution paths, finding real entry points, and gathering evidence before edits.

Use in Codex:
- `Use the explorer agent to trace how login works.`

### `reviewer`

Purpose:
- High-scrutiny review for correctness, regressions, security, and missing tests.
- Best for PR review or validating risky changes.

Use in Codex:
- `Use the reviewer agent on the current branch.`

### `docs_researcher`

Purpose:
- Verifies APIs and behavior against primary documentation.
- Best for framework upgrades, SDK changes, and uncertain docs claims.

Use in Codex:
- `Use docs_researcher to confirm the official Next.js behavior before editing.`

## Enabled Plugin

### `github@openai-curated`

Purpose:
- GitHub repository, PR, issue, commit, review, and branch workflows.
- Best for repo triage, PR review, issue handling, and CI-oriented tasks.

Use in Codex:
- `Summarize PR #45.`
- `List requested changes on this PR.`
- `Open a bug issue for this regression.`

## MCP Servers

### `github`

Purpose:
- GitHub API operations through MCP.

Use in Codex:
- Ask for PR, issue, branch, commit, or review actions.

### `context7`

Purpose:
- Up-to-date framework and library documentation lookup.

Use in Codex:
- Ask for official docs, examples, or API verification.

### `exa`

Purpose:
- Web search, code search, and page crawling.

Use in Codex:
- Ask for latest web info, code examples, or research.

### `memory`

Purpose:
- Persistent memory and project knowledge storage.

Use in Codex:
- Ask Codex to remember conventions, decisions, or long-lived facts.

### `playwright`

Purpose:
- Browser automation, screenshots, navigation, and UI checks.

Use in Codex:
- Ask Codex to open pages, click flows, fill forms, or verify UI behavior.

### `sequential-thinking`

Purpose:
- Structured multi-step reasoning on difficult problems.

Use in Codex:
- Ask for a careful step-by-step breakdown or root-cause analysis.

## ECC In Your Codex

You currently have these ECC pieces installed globally:

- ECC instructions merged into `~/.codex/AGENTS.md`
- ECC command prompts in `~/.codex/prompts/`
- ECC command source files in `~/.codex/ecc/commands/`
- ECC rule source files in `~/.codex/ecc/rules/`
- ECC Codex plugin manifest staged in `~/.codex/ecc/plugin/`

Important note:

- The ECC plugin manifest is present locally, but your current Codex CLI does not expose a general `plugin install` command, so ECC is currently most useful through its prompts, rules, and shared skills rather than as an actively installed marketplace plugin.

## ECC Prompts You Have

These are effectively your current ECC command layer inside Codex.

### Planning and orchestration

- `ecc-plan`: break a task into an execution plan.
- `ecc-orchestrate`: coordinate a broader workflow.
- `ecc-multi-plan`: multi-agent planning.
- `ecc-multi-execute`: multi-agent execution.
- `ecc-multi-backend`: backend-oriented multi-agent workflow.
- `ecc-multi-frontend`: frontend-oriented multi-agent workflow.
- `ecc-multi-workflow`: larger combined workflow orchestration.
- `ecc-devfleet`: DevFleet-style multi-agent execution.
- `ecc-loop-start`: start an autonomous loop.
- `ecc-loop-status`: inspect loop status.
- `ecc-pm2`: PM2-oriented operational workflow.
- `ecc-projects`: project inventory or project-management helper workflow.

### Review, verification, and quality

- `ecc-code-review`: code review workflow.
- `ecc-verify`: full verification workflow.
- `ecc-quality-gate`: gate a change before merge.
- `ecc-eval`: evaluation workflow.
- `ecc-learn-eval`: learning-oriented evaluation.
- `ecc-test-coverage`: coverage-focused workflow.
- `ecc-tool-run-tests`: generic test-run helper prompt.
- `ecc-tool-check-coverage`: generic coverage helper prompt.
- `ecc-tool-security-audit`: generic security audit helper prompt.
- `ecc-skill-health`: audit skill quality/health.
- `ecc-rules-distill`: turn repeated patterns into rules.

### Build, language, and debugging

- `ecc-build-fix`: fix build failures.
- `ecc-gradle-build`: Gradle build/debug flow.
- `ecc-python-review`: Python review flow.
- `ecc-go-build`, `ecc-go-review`, `ecc-go-test`: Go build/review/test flows.
- `ecc-rust-build`, `ecc-rust-review`, `ecc-rust-test`: Rust build/review/test flows.
- `ecc-cpp-build`, `ecc-cpp-review`, `ecc-cpp-test`: C++ build/review/test flows.
- `ecc-kotlin-build`, `ecc-kotlin-review`, `ecc-kotlin-test`: Kotlin build/review/test flows.

### Session and memory workflows

- `ecc-save-session`: save session context.
- `ecc-resume-session`: resume prior session context.
- `ecc-sessions`: browse session history.
- `ecc-checkpoint`: create a checkpoint.
- `ecc-prune`: reduce context/noise.
- `ecc-context-budget`: audit context consumption.
- `ecc-aside`: move side topics out of the critical path.
- `ecc-claw`: NanoClaw/ECC-style REPL workflow.

### Learning and instinct workflows

- `ecc-learn`: capture learned patterns.
- `ecc-evolve`: evolve learned patterns into reusable assets.
- `ecc-instinct-status`: inspect instinct system status.
- `ecc-instinct-import`: import instincts.
- `ecc-instinct-export`: export instincts.
- `ecc-promote`: promote patterns into reusable artifacts.

### Docs and maintenance

- `ecc-docs`: docs lookup and update workflow.
- `ecc-update-docs`: refresh docs.
- `ecc-update-codemaps`: refresh code maps.
- `ecc-setup-pm`: package manager setup helper.
- `ecc-prompt-optimize`: optimize prompts.
- `ecc-refactor-clean`: cleanup/refactor workflow.
- `ecc-skill-create`: create a new skill.
- `ecc-harness-audit`: audit harness quality and setup.

### Rule-pack prompts

- `ecc-rules-pack-common`
- `ecc-rules-pack-typescript`
- `ecc-rules-pack-python`
- `ecc-rules-pack-golang`
- `ecc-rules-pack-swift`

Use in Codex:

- `Use ecc-verify on this repository.`
- `Use ecc-rules-pack-common and ecc-code-review on the current diff.`

## High-Frequency Recommendations

These are the ones I expect to be useful most often in a real Codex workflow.

### Agents

- `explorer`: use before risky edits.
- `reviewer`: use before merge or after non-trivial changes.
- `docs_researcher`: use whenever framework behavior may have changed.

### Skills

- `tdd-workflow`: best default for feature work and bug fixes.
- `verification-loop`: excellent pre-merge safety net.
- `security-review`: use on auth, API, secrets, uploads, billing, or user input.
- `documentation-lookup`: best when libraries/frameworks are involved.
- `search-first`: use before inventing custom implementations.
- `coding-standards`: good default for TS/JS work.
- `frontend-patterns`: strong default for React/Next.js work.
- `backend-patterns`: strong default for API/server work.
- `e2e-testing`: useful for UI and critical user flows.
- `design-system`: useful when touching styling or component consistency.
- `repo-scan`: useful when entering an unfamiliar repo.
- `codebase-onboarding`: great for quickly understanding a new project.
- `benchmark`: useful before and after performance-sensitive changes.
- `database-migrations`: good for schema changes and rollout safety.
- `strategic-compact`: useful in long sessions to keep context healthy.

### Plugin and MCP

- `github` plugin: probably the highest-frequency external integration.
- `context7`: highest-value docs source.
- `exa`: highest-value research/search source.
- `playwright`: highest-value browser/UI tool.

### ECC prompts

- `ecc-plan`
- `ecc-code-review`
- `ecc-verify`
- `ecc-build-fix`
- `ecc-context-budget`
- `ecc-save-session`
- `ecc-resume-session`
- `ecc-rules-pack-common`
- `ecc-rules-pack-typescript`
- `ecc-tool-run-tests`

## Complete Skill Catalog

Usage rule for every skill below:

- In Codex, the normal way to use a skill is to mention it by name in your request.
- Example format: `Use <skill-name> for this task.`

### Core engineering, workflow, and quality

- `agent-eval`: compare coding agents on pass rate, cost, time, and consistency.
- `agent-harness-construction`: optimize agent action spaces and tool definitions.
- `agentic-engineering`: run eval-first, decomposed, cost-aware engineering workflows.
- `ai-first-engineering`: working model for teams with heavy AI implementation.
- `ai-regression-testing`: regression strategies tailored to AI-assisted development.
- `architecture-decision-records`: capture architectural decisions as ADRs.
- `benchmark`: measure performance baselines and regressions.
- `blueprint`: turn a large objective into a multi-step delivery plan.
- `ck`: project memory and session persistence helpers.
- `codebase-onboarding`: map an unfamiliar repo and generate onboarding context.
- `coding-standards`: general TS/JS/React/Node coding standards.
- `configure-ecc`: install or verify Everything Claude Code.
- `context-budget`: audit context usage and token waste.
- `continuous-agent-loop`: patterns for continuous autonomous loops.
- `continuous-learning`: extract reusable patterns from past work.
- `continuous-learning-v2`: instinct-based learning that evolves into assets.
- `cost-aware-llm-pipeline`: optimize cost and routing for LLM usage.
- `deployment-patterns`: deployment, CI/CD, health checks, and rollback practices.
- `documentation-lookup`: fetch up-to-date official docs via Context7.
- `enterprise-agent-ops`: operate long-lived agent systems safely.
- `eval-harness`: formal eval-driven development patterns.
- `git-workflow`: branch, merge, rebase, and collaboration patterns.
- `iterative-retrieval`: progressively refine context retrieval.
- `plankton-code-quality`: write-time formatting/lint/code quality enforcement.
- `product-lens`: validate the product rationale before building.
- `project-guidelines-example`: template for project-specific instruction design.
- `prompt-optimizer`: improve prompts rather than executing the task directly.
- `ralphinho-rfc-pipeline`: RFC-driven DAG execution with quality gates.
- `repo-scan`: audit a codebase and classify modules/assets.
- `rules-distill`: distill patterns into durable rules.
- `safety-guard`: prevent destructive actions in risky environments.
- `santa-method`: adversarial multi-agent verification before shipping.
- `search-first`: research before implementing.
- `security-review`: security review for sensitive code or flows.
- `security-scan`: scan agent/config setups for security issues.
- `skill-comply`: evaluate whether skills and rules are actually being followed.
- `skill-stocktake`: audit skills/commands quality.
- `strategic-compact`: compact context at logical milestones.
- `tdd-workflow`: enforce test-first work with strong coverage expectations.
- `team-builder`: compose and dispatch agent teams.
- `token-budget-advisor`: help control answer depth and token usage.
- `verification-loop`: broad verification loop before release or merge.

### Web, frontend, design, and browser

- `browser-qa`: browser-driven UI verification.
- `canary-watch`: monitor deployed URLs for regressions.
- `click-path-audit`: trace full button/touchpoint state-change paths.
- `content-hash-cache-pattern`: cache expensive file processing by content hash.
- `design-system`: generate or audit design systems and consistency.
- `e2e-testing`: Playwright E2E testing patterns.
- `frontend-patterns`: React/Next.js/frontend architecture patterns.
- `frontend-slides`: build HTML presentations and convert PPTX flows.
- `liquid-glass-design`: iOS 26 Liquid Glass design patterns.
- `nextjs-turbopack`: Next.js 16+/Turbopack patterns.
- `nuxt4-patterns`: Nuxt 4 SSR-safe app patterns.

### Backend, API, database, and infrastructure

- `api-design`: REST API design patterns and conventions.
- `backend-patterns`: backend architecture and server best practices.
- `clickhouse-io`: ClickHouse analytics and query optimization.
- `database-migrations`: safe schema/data migration patterns.
- `docker-patterns`: Docker and Compose practices.
- `jpa-patterns`: JPA/Hibernate entity and query patterns.
- `mcp-server-patterns`: build MCP servers with Node/TypeScript.
- `postgres-patterns`: PostgreSQL schema, indexing, and query patterns.

### Language and framework skills

- `android-clean-architecture`: Android/KMP clean architecture.
- `bun-runtime`: Bun runtime and package-manager guidance.
- `claude-api`: Claude API patterns for Python and TypeScript.
- `compose-multiplatform-patterns`: Compose Multiplatform UI patterns.
- `cpp-coding-standards`: modern C++ standards guidance.
- `cpp-testing`: C++ testing and coverage workflows.
- `django-patterns`: Django architecture and DRF patterns.
- `django-security`: Django security practices.
- `django-tdd`: Django testing/TDD.
- `django-verification`: Django verification loop.
- `flutter-dart-code-review`: Flutter/Dart review checklist.
- `foundation-models-on-device`: Apple on-device foundation model framework.
- `golang-patterns`: idiomatic Go engineering patterns.
- `golang-testing`: Go testing patterns.
- `java-coding-standards`: Java/Spring Boot coding standards.
- `kotlin-coroutines-flows`: Kotlin coroutines and Flow patterns.
- `kotlin-exposed-patterns`: Exposed ORM patterns.
- `kotlin-ktor-patterns`: Ktor server patterns.
- `kotlin-patterns`: idiomatic Kotlin engineering guidance.
- `kotlin-testing`: Kotlin testing patterns.
- `laravel-patterns`: Laravel architecture patterns.
- `laravel-plugin-discovery`: discover and evaluate Laravel packages.
- `laravel-security`: Laravel security.
- `laravel-tdd`: Laravel TDD and testing.
- `laravel-verification`: Laravel verification loop.
- `perl-patterns`: modern Perl patterns.
- `perl-security`: Perl security practices.
- `perl-testing`: Perl testing strategies.
- `python-patterns`: Python idioms and engineering guidance.
- `python-testing`: pytest/TDD/testing strategy.
- `pytorch-patterns`: PyTorch training and modeling patterns.
- `rust-patterns`: Rust architecture and language patterns.
- `rust-testing`: Rust testing patterns.
- `springboot-patterns`: Spring Boot architecture patterns.
- `springboot-security`: Spring Boot security practices.
- `springboot-tdd`: Spring Boot TDD/testing.
- `springboot-verification`: Spring Boot verification loop.
- `swift-actor-persistence`: actor-based persistence in Swift.
- `swift-concurrency-6-2`: Swift 6.2 concurrency patterns.
- `swift-protocol-di-testing`: protocol DI and testing in Swift.
- `swiftui-patterns`: SwiftUI architecture and UI patterns.

### Research, docs, and knowledge work

- `article-writing`: polished long-form writing in a target voice.
- `deep-research`: multi-source cited research.
- `exa-search`: neural search for web, code, and company research.
- `investor-materials`: investor-facing decks, memos, and models.
- `investor-outreach`: investor emails and outreach.
- `market-research`: market, competitor, and diligence research.

### Content, social, and media

- `content-engine`: build platform-native content systems.
- `crosspost`: adapt content across multiple social platforms.
- `fal-ai-media`: generate image, video, and audio via fal.ai.
- `video-editing`: AI-assisted editing workflows.
- `videodb`: understand and manipulate video/audio content.
- `visa-doc-translate`: bilingual visa-document translation.
- `x-api`: X/Twitter API integration patterns.

### Multi-agent and orchestration

- `autonomous-loops`: architectures for autonomous loops.
- `claude-devfleet`: orchestrate multi-agent coding via DevFleet.
- `dmux-workflows`: tmux-based multi-agent orchestration.
- `nanoclaw-repl`: operate ECC's NanoClaw REPL.

### Healthcare and regulated domains

- `healthcare-cdss-patterns`: clinical decision support patterns.
- `healthcare-emr-patterns`: EMR/EHR workflow patterns.
- `healthcare-eval-harness`: patient-safety-oriented evaluation harness.
- `healthcare-phi-compliance`: PHI/PII handling and compliance.

### Business, operations, and domain-specialist skills

- `agent-payment-x402`: add x402 payment execution for agents.
- `carrier-relationship-management`: freight carrier portfolio and negotiation workflows.
- `customs-trade-compliance`: import/export compliance and tariff workflows.
- `data-scraper-agent`: build AI-powered public-data collection agents.
- `energy-procurement`: electricity and gas procurement strategy.
- `inventory-demand-planning`: demand forecasting and replenishment planning.
- `logistics-exception-management`: handle delays, damages, losses, and disputes.
- `nutrient-document-processing`: OCR, extract, redact, fill, sign, and convert documents.
- `production-scheduling`: manufacturing scheduling and bottleneck management.
- `quality-nonconformance`: NCR, CAPA, supplier quality, and root-cause analysis.
- `regex-vs-llm-structured-text`: choose regex vs LLM for structured parsing.
- `returns-reverse-logistics`: returns, refund, and warranty workflows.

## Suggested Personal Defaults

If you want a compact default operating style in Codex, this is a strong baseline:

- For code changes: `tdd-workflow` + `verification-loop`
- For libraries/frameworks: `documentation-lookup`
- For risky surfaces: `security-review`
- For web research: `exa-search`
- For unfamiliar repos: `repo-scan` or `codebase-onboarding`
- For UI changes: `frontend-patterns` + `e2e-testing`
- For backend/API work: `backend-patterns` + `api-design`
- For final validation: `reviewer` agent + `ecc-verify`
