---
name: alibaba-java-release-check
description: 在 Java 变更完成前、提交前、发布前、代码审查收尾、依赖版本变更、二方库升级和工程结构检查时使用。
---

# 阿里 Java 完成检查

## 使用方式

用于收尾阶段的强制项汇总检查。读取 `references/mandatory-rules.md`，再按本次变更涉及的阶段回到相关叶子 skill 做补查。

## 检查流程

1. 回顾本次变更涉及的文件类型：Java、Controller、Mapper、SQL、测试、pom、配置。
2. 对照总控 skill，确认已加载所有相关叶子 skill。
3. 检查二方库、GAV、版本号、SNAPSHOT、依赖仲裁、重复依赖和枚举返回值限制。
4. 确认核心增量代码已运行测试；未运行时必须说明原因。
5. 汇总仍需用户决策的破坏性接口变更、数据库迁移风险、兼容性风险。

## 输出要求

最终答复必须包含：

- 使用过的阿里 Java 子 skill。
- 验证命令和结果。
- 未覆盖的风险或阻塞。

## 详细规则

见 `references/mandatory-rules.md`。
