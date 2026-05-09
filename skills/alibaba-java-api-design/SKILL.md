---
name: alibaba-java-api-design
description: 在设计 Java 后端模块、包结构、接口、Service/DAO、DTO/DO/VO、二方库坐标或跨模块契约时使用，尤其需要遵循阿里 Java 强制规约时使用。
---

# 阿里 Java 接口设计

## 使用方式

用于编码前的结构和契约检查。读取 `references/mandatory-rules.md`，把命中的规则转成设计约束，再开始实现。

## 检查流程

1. 识别新增或修改的包、类、接口、DTO/DO/VO、Service、DAO、Mapper、依赖坐标。
2. 检查命名是否符合英文语义、大小写、后缀、布尔属性和包名规则。
3. 检查对外接口签名是否兼容；二方库或外部调用方依赖的接口不得随意改签名。
4. 检查 Service/DAO 对外暴露是否先定义接口，内部实现是否使用 `Impl` 后缀。
5. 如涉及 Maven 坐标、版本、二方库依赖，追加使用 `alibaba-java-release-check`。

## 输出要求

在设计或审查结论中明确列出：

- 命中的强制规则类别。
- 必须改名、拆层或保持兼容的对象。
- 需要用户确认的破坏性接口变更。

## 详细规则

见 `references/mandatory-rules.md`。
