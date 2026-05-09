---
name: alibaba-java-development
description: 在 Java 或 Spring 后端开发、修改、审查、测试、数据库变更、接口安全检查、发布前检查时使用，尤其当用户要求遵循阿里巴巴 Java 开发手册或阿里 Java 强制规约时使用。
---

# 阿里 Java 开发总控

## 核心原则

这是一个路由型 skill。先判断当前任务涉及哪些开发阶段，再只加载相关叶子 skill；不要一次性加载全部规则。

本组 skill 内置一份离线规则集，整理自《阿里巴巴 Java 开发手册》`1.3.0` 版的 `【强制】` 条款，并做了规约化转述。使用时不需要访问任何外部文件。

## 路由流程

1. 先识别任务类型，按下表选择叶子 skill。
2. 只读取命中的叶子 skill 及其 `references/mandatory-rules.md`。
3. 如果任务跨多个阶段，按“设计 -> 编码 -> 数据库/安全/异常日志 -> 测试 -> 完成检查”的顺序使用。
4. 修改代码时，将命中规则转化为具体检查项；完成前报告遵循了哪些 skill，哪些规则需要用户取舍。

## 任务到 skill 的映射

| 任务线索 | 必须加载 |
|---|---|
| 新模块、新接口、包结构、DTO/DO/VO、Service/DAO、依赖坐标 | `alibaba-java-api-design` |
| 编写或修改 Java 业务代码、实体、DTO、工具类、服务实现 | `alibaba-java-implementation` |
| try/catch、事务回滚、finally、日志输出、日志框架 | `alibaba-java-exception-logging` |
| 单元测试、新增测试、修复测试、验证核心增量代码 | `alibaba-java-unit-testing` |
| Controller、HTTP API、用户输入、权限、脱敏、防注入、CSRF、XSS、防重放 | `alibaba-java-security` |
| 建表、改表、索引、SQL、分页、Mapper、MyBatis/ORM 映射 | `alibaba-java-mysql` |
| 完成前审查、提交前检查、发布前检查、依赖版本、SNAPSHOT、二方库 | `alibaba-java-release-check` |

## 默认组合

- 新功能开发：`api-design` -> `implementation` -> 按需 `mysql/security/exception-logging` -> `unit-testing` -> `release-check`
- Bug 修复：`implementation` -> 按根因追加 `mysql/security/exception-logging` -> `unit-testing`
- 代码审查：先用任务相关叶子 skill，再用 `release-check` 汇总强制项风险。
- 数据库相关变更：`mysql` 必须加载；如果 SQL 参数来自用户输入，再加载 `security`。
- Web 接口变更：`security` 必须加载；如果涉及响应异常和日志，再加载 `exception-logging`。

## 渐进式披露要求

当你通过此总控 skill 命中某个叶子 skill 时，必须读取该叶子 skill 的 `SKILL.md`。如果需要具体强制项，再读取该叶子 skill 的 `references/mandatory-rules.md`。

不要把未命中阶段的规则加载进上下文。
