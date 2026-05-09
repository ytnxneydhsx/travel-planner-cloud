# 基于《阿里巴巴 Java 开发手册》的项目问题清单

## 说明

本文基于阿里官方公开材料，对当前仓库进行规范性检查整理：

- `alibaba/p3c` 仓库 README（中文最新版本说明为黄山版，2022-02-03 发布）
- 官方 GitBook：
  - 异常处理
  - 日志规约
  - 应用分层
  - 安全规约
  - 工程规约 / 建表规约

按当前要求，**本文先不包含“测试覆盖率 / 单元测试不足”这一项**，只保留其他需要处理的问题。

## 必须修复

### 1. 行程模块和用户模块存在水平越权风险（已开始修复）

问题描述：

- `itinerary-service` 原实现直接信任请求体中的 `userId`。
- 行程的查询、更新、删除、添加目的地、移除目的地，当前都只按路径参数 `id` 处理，没有校验“当前用户是否拥有该行程”。
- `user-service` 原有的按 ID 查询用户信息接口同样可以被直接枚举访问。

涉及位置：

- `itinerary-service/src/main/java/org/example/itineraryservice/dto/ItineraryCreateRequest.java:19`
- `itinerary-service/src/main/java/org/example/itineraryservice/controller/ItineraryController.java:42`
- `itinerary-service/src/main/java/org/example/itineraryservice/controller/ItineraryController.java:56`
- `itinerary-service/src/main/java/org/example/itineraryservice/controller/ItineraryController.java:66`
- `itinerary-service/src/main/java/org/example/itineraryservice/controller/ItineraryController.java:75`
- `itinerary-service/src/main/java/org/example/itineraryservice/controller/ItineraryController.java:85`
- `user-service/src/main/java/org/example/userservice/controller/UserController.java:40`

对应手册要点：

- 安全规约：用户个人页面或功能必须做权限控制校验。
- 安全规约：用户请求传入的任何参数必须做有效性验证。
- 应用分层：安全控制应在网关 / Web 入口统一处理，不应把用户身份完全交给客户端传参。

本次已完成的修复：

- `itinerary-service` 创建行程时，已不再从请求体读取 `userId`，改为由请求头 `X-User-Id` 提供当前用户标识。
- `itinerary-service` 的查询详情、查询列表、更新、删除、添加目的地、移除目的地接口，已统一改为基于当前用户上下文执行，只允许访问自己的行程。
- `itinerary-service` 服务层新增资源归属校验逻辑：行程不存在返回 `404`，行程不属于当前用户返回 `403`。
- `user-service` 已将“查询自己信息”接口从 `GET /users/{id}` 调整为 `GET /users/me`，避免让客户端再次显式传入自己的用户 ID。
- `user-service` 当前通过请求头 `X-User-Id` 识别当前用户，作为后续接入网关和 JWT 透传前的临时过渡方案。

本次修复对应的手册规范：

- 安全规约：**用户个人页面或者功能必须进行权限控制校验。**
- 安全规约：**用户请求传入的任何参数必须做有效性验证。**
- 应用分层：**Web 层负责前置访问控制、基础参数校验。**
- 应用分层：**安全控制最终应收口到网关 / 开放接口层。**

当前仍然存在的边界：

- 这次修复解决的是“不能再直接通过 body / path 参数伪造资源归属”的问题。
- 当前用户身份仍然来自请求头 `X-User-Id`，本质上仍属于临时方案。
- 要完全符合阿里手册语境下的安全闭环，后续仍需接入 `gateway-service`，由网关解析 JWT 后透传可信用户上下文。

### 2. 行程模块多表写操作缺失事务边界（已开始修复）

问题描述：

- 创建行程时，先写 `itineraries`，再逐条写 `itinerary_destinations`。
- 中途如果远程校验失败、数据库写入失败，主表可能已经成功，关联表只写了一部分。
- 删除行程时，先删关联表，再删主表，也缺少统一事务保护。

涉及位置：

- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:48`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:98`

对应手册要点：

- 事务不是越多越好，但需要事务的地方必须考虑回滚和一致性。
- 多步数据库操作属于典型的事务边界场景。

本次已完成的修复：

- `itinerary-service` 的 `create()` 已补充事务注解，确保主表和关联表写入失败时统一回滚。
- `itinerary-service` 的 `deleteById()` 已补充事务注解，确保删除主表和删除关联表属于同一事务边界。
- 创建流程已调整为：先完成目的地存在性校验，再进入数据库写入阶段，减少半成品数据落库风险。

当前仍然存在的边界：

- 事务边界目前主要补在“创建行程”和“删除行程”两个多表写操作上。
- 如果后续引入更多跨表写操作，仍需逐一检查是否需要纳入事务。

### 3. 添加目的地存在并发竞争窗口（已开始修复）

问题描述：

- `addDestination()` 先查重，再插入。
- 并发请求下，两个请求可能同时查到“不存在”，随后一个成功、另一个撞数据库唯一索引。
- 当前没有把这类数据库异常翻译为业务可理解错误，最终很可能落成 `500`。

涉及位置：

- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:104`
- `itinerary-service/src/main/resources/db/migration/V1__create_itineraries_tables.sql:19`

对应手册要点：

- 并发处理需要保证共享资源访问的原子性。
- 异常处理不能把底层异常直接暴露成无语义的系统错误。

本次已完成的修复：

- `itinerary-service` 的 `addDestination()` 已补充事务注解，使该业务动作具备明确的写入边界。
- 保留应用层前置查重的同时，已增加对数据库唯一索引冲突的捕获与翻译。
- 当并发请求同时添加同一个目的地时，数据库若抛出唯一键冲突，当前实现会稳定返回 `409 Conflict`，不再落成通用 `500`。

当前仍然存在的边界：

- 当前实现的并发兜底主要依赖数据库唯一索引和异常翻译。
- 如果后续还要支持更复杂的顺序调整、批量插入、批量替换等场景，仍需重新评估并发控制策略。

### 4. Service 层直接抛 HTTP 异常，分层职责混乱（已开始修复）

问题描述：

- `Service` 层直接抛 `ResponseStatusException`，把 HTTP 传输语义带进业务层。
- 这会导致业务层和 Web 层强耦合，不利于后续接入网关、RPC、统一异常模型。

涉及位置：

- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:38`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:56`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:59`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:109`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:130`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:144`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:151`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:166`

对应手册要点：

- 应用分层中，Web 层负责对外协议转换。
- 应用内部更适合使用业务异常，而不是直接抛 HTTP 状态异常。

本次已完成的修复：

- `common-web` 已新增统一业务异常 `BusinessException`，用于在应用内部表达业务失败。
- `common-web` 的全局异常处理器已补充 `BusinessException` 转换逻辑，由 Web 层统一负责将业务异常映射为 HTTP 响应。
- `user-service` 的 `UserServiceImpl` 已不再直接抛 `ResponseStatusException`，改为抛出 `BusinessException`。
- `itinerary-service` 的 `ItineraryServiceImpl` 已不再直接抛 `ResponseStatusException`，改为抛出 `BusinessException`。

当前仍然存在的边界：

- 当前仍有部分 Controller 层使用 `ResponseStatusException`，这是当前阶段允许的过渡状态，因为对外协议转换仍在 Web 层。
- 后续如果继续统一异常风格，可以进一步将 Controller 层的错误分支也逐步收敛到统一异常模型。

## 建议修改

### 5. 全局异常处理器吞掉异常，但没有日志（已开始修复）

问题描述：

- 全局异常处理器把未知异常统一转成 `500`。
- 但没有记录任何堆栈、请求上下文、关键参数，线上排障困难。

涉及位置：

- `common-web/src/main/java/org/example/common/web/BaseGlobalExceptionHandler.java:32`

对应手册要点：

- 日志规约：异常日志必须包含足够的定位信息和异常堆栈。

本次已完成的修复：

- `common-web` 的全局异常处理器已补充日志输出。
- 参数校验失败、业务异常、`ResponseStatusException` 都会记录结构化的 `warn` 日志。
- 未捕获异常现在会记录带堆栈的 `error` 日志，不再只是静默返回 `500`。

### 6. 仓库没有统一接入 SLF4J 日志（已开始修复）

问题描述：

- 当前代码中没有 `Logger` / `LoggerFactory` / `@Slf4j`。
- 远程调用失败、参数校验失败、用户登录失败、字典加载失败等关键路径，没有可审计日志。

涉及位置：

- `common-web/src/main/java/org/example/common/web/BaseGlobalExceptionHandler.java:33`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:156`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:53`
- `destination-service/src/main/java/org/example/destinationservice/support/region/RegionDictionary.java:45`

对应手册要点：

- 日志规约：统一使用 SLF4J 门面，不直接依赖具体日志实现 API。
- 异常信息需要记录案发现场。

本次已完成的修复：

- `common-web`、`user-service`、`itinerary-service`、`destination-service` 已开始接入 SLF4J。
- `RegionDictionary` 的字典加载成功与失败已补充日志。
- `UserServiceImpl` 的注册冲突、登录失败、禁用账户登录已补充 `warn` 日志。
- `ItineraryServiceImpl` 的权限拒绝、资源不存在、目的地重复添加、远程调用失败已补充 `warn/error` 日志。

当前仍然存在的边界：

- 目前日志补充优先覆盖了异常处理与高风险失败路径，还没有覆盖所有业务成功路径。
- 如果后续继续对齐手册，可以进一步补充统一的访问日志、关键审计日志和 trace 信息。

### 7. 数据库表结构不符合阿里手册建表强制规约

问题描述：

- 表名使用了复数名词。
- 审计字段使用 `created_at / updated_at`，未统一为 `gmt_create / gmt_modified`。
- 主键字段未采用手册里常见的 `unsigned bigint` 约定。

涉及位置：

- `user-service/src/main/resources/db/migration/V1__create_users_table.sql:1`
- `destination-service/src/main/resources/db/migration/V1__create_destinations_table.sql:1`
- `itinerary-service/src/main/resources/db/migration/V1__create_itineraries_tables.sql:1`

对应手册要点：

- 表名不使用复数名词。
- 表必备基础字段应统一。
- 主键类型应统一约定，避免后续扩展隐患。

### 8. 更新接口允许“空更新请求”进入动态 SQL

问题描述：

- `DestinationUpdateRequest` 和 `ItineraryUpdateRequest` 的字段全部可空。
- 对应 MyBatis `update` 使用动态 `<set>`。
- 当请求体所有字段都为空时，最终可能生成非法 SQL 或转成无意义更新请求。

涉及位置：

- `destination-service/src/main/java/org/example/destinationservice/dto/DestinationUpdateRequest.java:16`
- `destination-service/src/main/java/org/example/destinationservice/mapper/DestinationMapper.java:71`
- `itinerary-service/src/main/java/org/example/itineraryservice/dto/ItineraryUpdateRequest.java:13`
- `itinerary-service/src/main/java/org/example/itineraryservice/mapper/ItineraryMapper.java:48`

对应手册要点：

- 用户输入参数必须做有效性校验。
- Web 层应拦住明显不合法请求，而不是把错误放大到数据库层。

## 问题待确认

### 9. 配置文件中存在弱默认值

问题描述：

- 三个服务的数据库密码默认值都是 `123456`。
- `user-service` 的 JWT 密钥也保留了可工作的默认占位值。

涉及位置：

- `destination-service/src/main/resources/application.properties:9`
- `user-service/src/main/resources/application.properties:9`
- `user-service/src/main/resources/application.properties:21`
- `itinerary-service/src/main/resources/application.properties:9`

说明：

这明显低于生产安全基线，但阿里手册公开条目里没有我当前核对到的“禁止弱默认口令 / 默认密钥”逐条硬规定，因此先标记为“待确认但应尽快处理”。

### 10. Feign 远程调用直接放在 Service 层，未单独封装

问题描述：

- `itinerary-service` 直接在 `ServiceImpl` 中发起 Feign 请求、判断结果、翻译异常。
- 当前没有单独的 `Manager` 层或远程调用包装层。

涉及位置：

- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:36`
- `itinerary-service/src/main/java/org/example/itineraryservice/service/impl/ItineraryServiceImpl.java:156`
- `itinerary-service/src/main/java/org/example/itineraryservice/client/DestinationClient.java:9`

说明：

阿里应用分层更推荐把第三方或外部服务调用封装到专门层中，用于统一处理返回值和异常。当前实现并非一定错误，但从规范角度看不够收敛。

## 仅供参考

### 11. 业务语义常量仍以魔法值散落

问题描述：

- 用户状态 `0 / 1` 在多处直接出现。
- `Bearer` 令牌类型字符串也直接硬编码。

涉及位置：

- `user-service/src/main/java/org/example/userservice/dto/UserRegisterRequest.java:29`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:45`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:58`
- `user-service/src/main/java/org/example/userservice/service/impl/UserServiceImpl.java:65`

对应手册要点：

- 常量应统一定义、命名清晰，避免业务语义分散在实现细节中。

## 当前优先级建议

如果后面按顺序逐个整改，建议优先级如下：

1. 水平越权 / 权限控制
2. 事务一致性
3. 异常模型与日志
4. 并发冲突处理
5. 更新接口参数校验
6. 表结构规约统一
7. 配置安全默认值
8. 远程调用分层收敛
9. 魔法值清理
