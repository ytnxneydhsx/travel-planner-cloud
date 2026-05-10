# gateway-service

## 模块定位

`gateway-service` 作为系统统一入口，负责路由转发和用户身份校验。

当前阶段建议先只承载以下稳定职责：

- 统一接收外部请求
- 按路径将请求转发到对应微服务
- 校验 `Authorization: Bearer <token>`
- 从 JWT 中解析当前用户身份
- 清理客户端伪造的 `X-User-Id`
- 向下游服务透传可信的 `X-User-Id`

## 设计原则

- 网关只负责入口控制和协议转发，不承载业务逻辑
- 用户身份以 JWT 为准，不再信任客户端直接传入的用户 ID
- 路由和白名单优先通过配置文件声明，减少硬编码分散
- 按能力分包：跨域放 `cors`，认证放 `security`，避免按“配置/工具/过滤器”横向拆散

## 为什么要先做网关

当前项目已经具备以下基础：

- `user-service` 可以签发 JWT
- `itinerary-service` 和 `user-service` 已经开始按“当前用户”思路收口接口
- 文档中已经明确指出，`X-User-Id` 目前只是过渡方案

当前项目还缺少的关键闭环是：

- 客户端传来的用户身份不可信
- 其他服务还没有统一校验 JWT
- 路由、鉴权、放行规则还分散在各服务思路里，没有真正的统一入口

所以在继续新增业务模块前，先设计网关更合理。

## 建议文件结构

```text
gateway-service
├─ pom.xml
└─ src
   └─ main
      ├─ java
      │  └─ org
      │     └─ example
      │        └─ gatewayservice
      │           ├─ GatewayServiceApplication.java
      │           ├─ cors
      │           │  ├─ GatewayCorsConfiguration.java
      │           │  └─ GatewayCorsProperties.java
      │           └─ security
      │              ├─ access
      │              │  └─ AccessControlProperties.java
      │              ├─ authentication
      │              │  ├─ AuthenticationConstants.java
      │              │  ├─ AuthenticatedUser.java
      │              │  ├─ JwtAuthenticationProperties.java
      │              │  └─ JwtTokenVerifier.java
      │              └─ filter
      │                 ├─ AuthenticatedUserHeaderRelayGlobalFilter.java
      │                 └─ JwtAuthenticationGlobalFilter.java
      │              └─ response
      │                 ├─ UnauthorizedResponse.java
      │                 └─ UnauthorizedResponseWriter.java
      └─ resources
         └─ application.properties
```

## 结构说明

- `GatewayServiceApplication`
  网关启动入口，后续只负责启动 Spring Cloud Gateway。
- `cors`
  只放跨域能力，包括跨域属性和 `CorsWebFilter` 注册配置。
- `security`
  只放认证能力。
  `security/access` 放访问控制配置，`security/authentication` 放 JWT 与认证上下文，
  `security/filter` 放网关认证过滤器，`security/response` 放统一认证失败响应模型与写出器。
- `resources/application.properties`
  放路由、端口、跨域、JWT 和访问控制配置。

## 第一版只做什么

为了适合学习，第一版网关建议只做 3 件事：

1. 路由转发
   先把 `/users/**`、`/destinations/**`、`/itineraries/**` 正确转发到对应服务。
2. 登录白名单
   放行 `/users/login` 和 `/users/register`，先不要求登录。
3. JWT 校验
   对其余受保护接口检查 `Authorization`，解析出用户 ID，再透传 `X-User-Id`。

## 先不要急着做的内容

- 注册中心
- 负载均衡
- 限流
- 熔断
- 分布式追踪
- 统一审计日志

这些都可以后面加，但不应该和第一版网关一起上。

## 学习顺序

建议按这个顺序一块一块写：

1. 先只写 `doc/gateway-service.md`
2. 再新建 `gateway-service` 空模块和 `pom.xml`
3. 再只配路由，不做 JWT
4. 路由跑通后，再加 JWT 校验
5. JWT 跑通后，再加白名单和透传 `X-User-Id`

这样每一步都能单独验证，不容易混乱。

## 当前边界

- 当前路由仍然通过固定 URL 指向 `user-service`、`destination-service`、`itinerary-service`
- 当前还没有接入注册中心，后续可以切换到服务发现
- 当前只放行注册和登录接口，其他受保护接口统一要求 JWT
- 当前已由网关统一重建并透传可信 `X-User-Id`，后续如有需要可以继续透传用户名、昵称或 trace 信息
- 当前未认证响应已统一为结构化 JSON，后续仍可继续扩展到 `403`、`429` 等场景

## 接下来网关 TODO

1. 请求头透传策略收口
   明确哪些请求头允许透传、哪些必须剥离、哪些应由网关统一重建。
2. 路由配置进一步整理
   后续根据服务数量和路由策略，继续收口静态路由配置的组织方式。
3. 网关日志
   增补基础访问日志、鉴权拒绝日志和关键转发日志，方便排障和审计。
