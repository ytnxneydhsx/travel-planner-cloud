# 阿里 Java 总控路由参考

## 阶段顺序

1. 设计：`alibaba-java-api-design`
2. 编码：`alibaba-java-implementation`
3. 专项：按变更内容选择 `alibaba-java-mysql`、`alibaba-java-security`、`alibaba-java-exception-logging`
4. 测试：`alibaba-java-unit-testing`
5. 完成：`alibaba-java-release-check`

## 常见组合

- Spring Controller CRUD：`api-design`、`implementation`、`security`、`mysql`、`exception-logging`、`unit-testing`、`release-check`
- Service 业务逻辑：`implementation`、按需 `exception-logging`、`unit-testing`、`release-check`
- Mapper 或 SQL 优化：`mysql`、如有用户参数则 `security`、`unit-testing`、`release-check`
- 新增模块：`api-design`、`implementation`、按需专项 skill、`unit-testing`、`release-check`
- 代码审查：根据文件类型加载对应专项 skill，最后使用 `release-check`
