# destination-service

## 模块定位

`destination-service` 负责目的地的发布、修改、删除、详情查询和列表查询。

当前阶段采用以下设计：

- 业务数据使用单表 `destination`
- 地区字典使用本地 `gb2260.json`
- 当前阶段不编写测试代码

## 文件结构

```text
destination-service
├─ pom.xml
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ org
│  │  │     └─ example
│  │  │        └─ destinationservice
│  │  │           ├─ DestinationServiceApplication.java
│  │  │           ├─ common
│  │  │           │  └─ ApiResponse.java
│  │  │           ├─ controller
│  │  │           │  └─ DestinationController.java
│  │  │           ├─ dto
│  │  │           │  ├─ DestinationCreateRequest.java
│  │  │           │  ├─ DestinationUpdateRequest.java
│  │  │           │  └─ DestinationResponse.java
│  │  │           ├─ entity
│  │  │           │  └─ Destination.java
│  │  │           ├─ mapper
│  │  │           │  └─ DestinationMapper.java
│  │  │           ├─ service
│  │  │           │  ├─ DestinationService.java
│  │  │           │  └─ impl
│  │  │           │     └─ DestinationServiceImpl.java
│  │  │           └─ support
│  │  │              └─ region
│  │  │                 ├─ RegionDictionary.java
│  │  │                 └─ RegionItem.java
│  │  └─ resources
│  │     ├─ application.properties
│  │     ├─ db
│  │     │  └─ migration
│  │     │     └─ V1__create_destinations_table.sql
│  │     └─ regions
│  │        └─ gb2260.json
```

## 结构说明

- `common`：放统一返回对象
- `controller`：对外提供 HTTP 接口
- `dto`：请求和响应模型
- `entity`：数据库实体对象
- `mapper`：MyBatis 注解式持久层接口
- `service`：业务逻辑层
- `support/region`：地区字典加载和解析
- `db/migration`：Flyway 数据库迁移脚本
- `regions/gb2260.json`：GB/T 2260 行政区划字典数据

## 当前实现边界

- 先完成景点 CRUD 主线
- `region_code` 存在 `destination` 表中
- 地区名称由本地 JSON 解析，不单独建 `regions` 表

## 最新说明

- 公共返回对象 `ApiResponse` 已抽取到 `common-web` 模块
- 当前服务保留自己的 `GlobalExceptionHandler`，用于后续扩展特殊异常处理
