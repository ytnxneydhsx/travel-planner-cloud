# user-service

## 模块定位

`user-service` 负责用户领域的基础业务，当前阶段优先完成以下主线：

- 用户注册
- 用户登录
- 用户信息查询

当前阶段先完成业务骨架，不编写测试代码，不接入统一鉴权网关。

## 文件结构

```text
user-service
├─ pom.xml
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ org
│  │  │     └─ example
│  │  │        └─ userservice
│  │  │           ├─ UserServiceApplication.java
│  │  │           ├─ common
│  │  │           │  ├─ ApiResponse.java
│  │  │           │  └─ GlobalExceptionHandler.java
│  │  │           ├─ controller
│  │  │           │  └─ UserController.java
│  │  │           ├─ dto
│  │  │           │  ├─ UserLoginRequest.java
│  │  │           │  ├─ UserLoginResponse.java
│  │  │           │  ├─ UserRegisterRequest.java
│  │  │           │  └─ UserResponse.java
│  │  │           ├─ entity
│  │  │           │  └─ User.java
│  │  │           ├─ mapper
│  │  │           │  └─ UserMapper.java
│  │  │           ├─ service
│  │  │           │  ├─ UserService.java
│  │  │           │  └─ impl
│  │  │           │     └─ UserServiceImpl.java
│  │  │           └─ support
│  │  │              ├─ jwt
│  │  │              │  └─ JwtTokenProvider.java
│  │  │              └─ password
│  │  │                 └─ PasswordEncoderSupport.java
│  │  └─ resources
│  │     ├─ application.properties
│  │     └─ db
│  │        └─ migration
│  │           └─ V1__create_users_table.sql
```

## 结构说明

- `common`：统一返回对象和全局异常处理
- `controller`：对外提供 HTTP 接口
- `dto`：注册、登录、JWT 返回模型
- `entity`：用户表实体对象
- `mapper`：MyBatis 注解式持久层接口
- `service`：用户业务逻辑
- `support/jwt`：JWT 生成能力
- `support/password`：密码处理辅助能力
- `db/migration`：Flyway 数据库迁移脚本

## 当前实现边界

- 当前先完成用户注册、登录、信息查询、JWT 签发
- 当前不接入统一网关鉴权
- 当前不实现复杂权限体系

## 最新说明

- 公共返回对象 `ApiResponse` 已抽取到 `common-web` 模块
- 当前服务保留自己的 `GlobalExceptionHandler`，用于后续扩展特殊异常处理
