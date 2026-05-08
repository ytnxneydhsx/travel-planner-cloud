# common-web

## 模块定位

`common-web` 用来承载多个微服务都需要的 Web 层公共能力。

当前只抽取以下稳定且重复的公共部分：

- `ApiResponse`
- `BaseGlobalExceptionHandler`

## 设计原则

- 只抽真正重复且稳定的部分
- 业务服务自己的特殊异常处理仍然保留在各自模块中
- 当前不抽业务相关 `support`

## 文件结构

```text
common-web
├─ pom.xml
└─ src
   └─ main
      └─ java
         └─ org
            └─ example
               └─ common
                  └─ web
                     ├─ ApiResponse.java
                     └─ BaseGlobalExceptionHandler.java
```

## 使用方式

- `destination-service`、`user-service` 通过 Maven 依赖引入 `common-web`
- 各服务保留自己的 `GlobalExceptionHandler`
- 各服务自己的 `GlobalExceptionHandler` 继承 `BaseGlobalExceptionHandler`
- 各服务的 `Controller` 直接使用 `org.example.common.web.ApiResponse`
