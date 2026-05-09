# itinerary-service

## 模块定位

`itinerary-service` 负责用户创建和管理行程，并通过调用 `destination-service` 校验目的地是否存在，再把目的地加入行程。

当前阶段采用以下设计：

- 业务数据使用 `itineraries` 和 `itinerary_destinations` 两张表
- 行程详情返回时实时查询目的地服务，补齐目的地摘要信息
- 当前阶段未接入正式网关鉴权，先通过请求头 `X-User-Id` 传入当前用户标识
- 创建行程时先校验目的地存在性，再在事务内写入行程主表和关联表

## 文件结构

```text
itinerary-service
├─ pom.xml
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ org
   │  │     └─ example
   │  │        └─ itineraryservice
   │  │           ├─ ItineraryServiceApplication.java
   │  │           ├─ client
   │  │           │  ├─ DestinationClient.java
   │  │           │  └─ dto
   │  │           │     └─ DestinationSummary.java
   │  │           ├─ common
   │  │           │  └─ GlobalExceptionHandler.java
   │  │           ├─ controller
   │  │           │  └─ ItineraryController.java
   │  │           ├─ dto
   │  │           │  ├─ ItineraryCreateRequest.java
   │  │           │  ├─ ItineraryDestinationAddRequest.java
   │  │           │  ├─ ItineraryDestinationResponse.java
   │  │           │  ├─ ItineraryResponse.java
   │  │           │  └─ ItineraryUpdateRequest.java
   │  │           ├─ entity
   │  │           │  ├─ Itinerary.java
   │  │           │  └─ ItineraryDestination.java
   │  │           ├─ mapper
   │  │           │  ├─ ItineraryDestinationMapper.java
   │  │           │  └─ ItineraryMapper.java
   │  │           └─ service
   │  │              ├─ ItineraryService.java
   │  │              └─ impl
   │  │                 └─ ItineraryServiceImpl.java
   │  └─ resources
   │     ├─ application.properties
   │     └─ db
   │        └─ migration
   │           └─ V1__create_itineraries_tables.sql
   └─ test
      └─ java
         └─ org
            └─ example
               └─ itineraryservice
                  └─ service
                     └─ impl
                        └─ ItineraryServiceImplTest.java
```

## 接口

- `POST /itineraries`：创建行程，可同时传入目的地 ID 列表
- `GET /itineraries/{id}`：查询当前用户自己的行程详情
- `GET /itineraries`：查询当前用户自己的行程列表
- `PUT /itineraries/{id}`：更新行程标题或描述
- `DELETE /itineraries/{id}`：删除行程
- `POST /itineraries/{id}/destinations`：向行程追加目的地
- `DELETE /itineraries/{id}/destinations/{destinationId}`：从行程移除目的地

以上受保护接口当前都要求请求头携带：

- `X-User-Id: <当前用户 ID>`

## 结构说明

- `client`：通过 OpenFeign 调用 `destination-service`
- `controller`：对外提供 HTTP 接口
- `dto`：请求和响应模型
- `entity`：数据库实体对象
- `mapper`：MyBatis 注解式持久层接口
- `service`：行程业务逻辑
- `db/migration`：Flyway 数据库迁移脚本

## 当前实现边界

- 创建行程时校验目的地是否存在
- 行程详情中只回填目的地摘要信息，不做本地快照
- 当前仅实现基于 `X-User-Id` 的临时归属校验，后续应由网关解析 JWT 后透传用户信息
