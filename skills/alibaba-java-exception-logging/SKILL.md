---
name: alibaba-java-exception-logging
description: 在新增或修改 Java 异常处理、try/catch/finally、事务回滚、资源关闭、日志框架、日志输出和异常堆栈记录时使用。
---

# 阿里 Java 异常日志

## 使用方式

用于异常处理和日志相关代码。读取 `references/mandatory-rules.md`，优先检查吞异常、错误回滚、finally 返回、资源泄漏、重复日志、日志占位符和日志框架依赖。

## 检查流程

1. 找出所有新增或修改的 `try/catch/finally`、`throw`、事务边界和日志语句。
2. 确认异常用于错误处理，而不是流程控制。
3. 确认 catch 块要么处理，要么继续抛出，不能静默丢弃。
4. 事务代码 catch 后如需回滚，必须显式处理回滚语义。
5. 日志必须包含现场信息和异常堆栈；debug/info 日志使用条件输出或占位符。
6. 如接口对外返回错误码或 HTTP 响应，结合项目约定说明错误语义。

## 输出要求

指出每个风险点对应的代码位置、违反的规则类别和建议修复方式。

## 详细规则

见 `references/mandatory-rules.md`。
