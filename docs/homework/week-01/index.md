# Week 01 - 开发环境与个人仓库

## 一、基本信息

- 课程：微服务架构与实践
- 姓名：施晨煜
- 学号：2412190325
- 操作系统：macOS 26.5
- 系统架构：Apple Silicon / arm64

## 二、环境检查

### 1. Java

执行命令：

```bash
java --version
运行结果：
openjdk 21.0.12.1 2026-08-18
OpenJDK Runtime Environment Homebrew (build 21.0.12.1)
OpenJDK 64-Bit Server VM Homebrew (build 21.0.12.1, mixed mode, sharing)
2. Maven
执行命令：
mvn --version
运行结果：
Apache Maven 3.9.16
Maven home: /opt/homebrew/Cellar/maven/3.9.16/libexec
Java version: 21.0.12.1, vendor: Homebrew
OS name: "mac os x", version: "26.5", arch: "aarch64"
3. Git
执行命令：
git --version
运行结果：
git version 2.52.0
4. Docker
执行命令：
docker version
主要版本信息：
Docker Client：29.7.2
Docker Server：29.7.2
Docker Desktop：4.87.0
5. Docker Compose
执行命令：
docker compose version
运行结果：
Docker Compose version v5.4.0

三、问题记录
初次检查时，Java、Maven 和 Docker 尚未安装。
后续通过 Homebrew 安装了 OpenJDK 21 和 Maven，并安装了 Docker Desktop。配置 JAVA_HOME 后，Java 与 Maven 均统一使用 OpenJDK 21。
目前 Java、Maven、Git、Docker 和 Docker Compose 均可以正常运行。
四、概念回答
1. 什么是微服务架构？
我理解的微服务架构，是把一个比较大的系统按照不同的业务功能拆分成多个相对独立的小服务。每个服务负责自己的一部分业务，例如用户、订单、支付等功能可以分别作为不同的服务。每个服务可以独立开发、测试和部署，服务之间再通过接口进行通信。
2. 微服务和单体架构的主要区别是什么？
单体架构通常会把系统的大部分功能放在一个项目中，最后一起开发和部署。在项目比较小时这种方式比较简单，但项目越来越大以后，各个模块之间的依赖也会越来越复杂。
微服务架构则会把不同业务模块拆成多个独立服务，每个服务可以单独开发和部署。这样模块边界会更加清楚，但同时也会增加服务通信、部署和运维方面的复杂度。
3. 为什么本课程先实现单体系统，再逐步拆分为微服务？
我认为先实现单体系统，可以先把重点放在业务功能和整体流程上，不需要一开始就处理服务之间通信、部署等额外问题。
等单体系统功能完成以后，再逐步拆分成微服务，就可以更直观地看到拆分前后的差别，也能更容易理解为什么需要进行服务拆分，以及拆分过程中可能遇到的问题。
4. 为什么作业需要提供可重复运行的测试或验证脚本？
因为只说程序可以运行，并不能证明其他人在相同环境下也能得到相同结果。
如果提供可以重复运行的测试或验证脚本，老师或其他开发者就可以快速检查程序功能是否正确。以后代码发生修改时，也可以重新运行测试，检查新改动有没有影响原来的功能，所以这样做能够提高项目的可靠性，也方便后续继续开发。