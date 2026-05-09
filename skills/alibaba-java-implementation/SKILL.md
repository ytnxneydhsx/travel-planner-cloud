---
name: alibaba-java-implementation
description: 在编写、修改或审查 Java 业务代码、实体、DTO、工具类、集合处理、并发逻辑、控制语句、注释和代码格式时使用。
---

# 阿里 Java 编码实现

## 使用方式

用于 Java 实现阶段。先读取 `references/mandatory-rules.md`，再针对改动文件逐项检查命名、常量、格式、OOP、集合、并发、控制语句、注释和其他基础编码强制项。

## 检查流程

1. 扫描新增和修改的 Java 文件，识别类、方法、字段、常量、集合、并发、控制语句、注释。
2. 先修正会影响框架解析、序列化、线程安全、集合安全的强制项。
3. 再修正格式、命名、注释、魔法值、过时 API 等可维护性强制项。
4. 如果涉及异常、日志、事务，追加 `alibaba-java-exception-logging`。
5. 如果涉及 SQL、Mapper、数据库字段，追加 `alibaba-java-mysql`。
6. 如果涉及 Controller 或用户输入，追加 `alibaba-java-security`。

## 输出要求

完成后说明：

- 哪些强制项已在代码中落实。
- 哪些强制项因现有框架或历史代码限制没有改动。
- 已运行的格式化、编译或测试命令。

## 详细规则

见 `references/mandatory-rules.md`。
