# Week 03 - 个人项目 Spring Boot 起步

## 本周计划

本周在 Week 02 确定的“桌游社活动管理与组队平台”基础上创建 Spring Boot 工程。

本周主要完成：

- 创建 Maven Spring Boot 工程；
- 配置 Java 25 和 Spring Boot 4.0.8；
- 使用 application.yml 完成基础配置；
- 添加 Spring Web 和 Actuator；
- 创建一个简单 GET 接口；
- 验证健康检查接口；
- 完成 Spring Boot 启动测试。

本周暂不实现具体业务模型、数据库、Service 和 Repository。

## 工程信息

- Java：25
- Spring Boot：4.0.8
- Maven：Maven Wrapper
- Group：com.zjgsu.scy
- Package：com.zjgsu.scy.monolith
- 配置文件：application.yml
- 默认端口：8080

## 启动测试

进入 `monolith/` 目录执行：

```bash
./mvnw test
```

测试结果：

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

说明 Spring Boot 应用上下文能够正常加载。

## 启动应用

执行：

```bash
./mvnw spring-boot:run
```

启动成功后，应用运行在：

`http://localhost:8080`

## GET 接口验证

访问：

`http://localhost:8080/api/hello`

返回：

```text
Hello, monolith
```

## 健康检查

访问：

`http://localhost:8080/actuator/health`

返回：

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

说明 Spring Boot Actuator 健康检查正常，应用状态为 `UP`。

## 截图记录

- `screenshots/01-mvn-test-success.png`：Maven 测试通过
- `screenshots/02-spring-boot-run-success.png`：Spring Boot 启动成功
- `screenshots/03-api-hello.png`：GET 接口访问结果
- `screenshots/04-actuator-health.png`：Actuator 健康检查结果

## 本周完成情况

已完成 Spring Boot 基础工程创建、配置、启动、GET 接口和健康检查，并通过 `@SpringBootTest` 的 `contextLoads` 测试。

当前尚未实现用户、桌游、活动、报名等具体业务能力，后续课程中再逐步增加。
