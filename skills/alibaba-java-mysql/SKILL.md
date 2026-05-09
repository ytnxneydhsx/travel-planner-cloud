---
name: alibaba-java-mysql
description: 在 Java 项目中设计或修改 MySQL 表结构、字段、索引、SQL、分页查询、Mapper、MyBatis 或 ORM 映射时使用。
---

# 阿里 Java MySQL 规约

## 使用方式

用于数据库设计和数据访问实现。读取 `references/mandatory-rules.md`，按建表、索引、SQL、ORM 映射四类检查。

## 检查流程

1. 建表或改字段时，检查命名、主键、必备字段、类型、保留字、小数类型和字符串长度。
2. 建索引或改查询时，检查唯一性、join 数量、字段类型一致性、varchar 索引长度和模糊查询。
3. 写 SQL 时，检查 `count`、`NULL`、分页、外键级联、存储过程、删除修改前查询确认。
4. 写 Mapper/ORM 时，检查禁止 `select *`、布尔字段映射、参数绑定、HashMap 返回和 `gmt_modified` 更新。
5. 如果 SQL 参数来自用户输入，追加使用 `alibaba-java-security`。

## 输出要求

数据库相关结论要明确到表、字段、索引、SQL 或 Mapper 语句，并说明违反或满足的强制项。

## 详细规则

见 `references/mandatory-rules.md`。
