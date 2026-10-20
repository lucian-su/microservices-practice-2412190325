# Week 05：桌游社项目 Docker 容器化（课后大作业）

姓名：施晨煜；学号：2412190325。沿用 Week 04 的 monolith/，校园选课课内工程单独运行，不纳入本周课后提交。

## Dockerfile 与镜像构建

monolith/Dockerfile 使用 Maven 3.9.16 + Temurin 25 编译并运行自动测试，运行阶段仅复制可执行 JAR 到 JRE 25 镜像。运行用户 UID 10001。
monolith/.dockerignore 排除构建产物、IDE 配置、密码和密钥文件。

```bash
docker build -t scy-boardgame:week05 ./monolith
docker image ls scy-boardgame:week05
```

## Compose 编排与启动

根目录 compose.yaml 包含 app 与 mysql:8.4.11。构建上下文为 ./monolith，应用通过 mysql 服务名连接数据库。命名卷 mysql-data 保存数据，首次初始化时执行原 Week 04 的 init.sql，满足 JPA validate 要求。健康检查通过后再启动应用。无 version 字段。

```bash
bash scripts/week05/setup-env.sh
docker compose config --quiet
docker compose up -d --build
docker compose ps
```

.env.example 提供变量模板，实际 .env 被忽略，不提交密码。MySQL 不映射宿主机端口，应用默认绑定 127.0.0.1:8080。

## 个人业务 API 验证

验证 GET /actuator/health、POST /api/activities、GET /api/activities/{id}，另创建并查询一条报名记录。创建响应应为 201，查询为 200。验证脚本用实际响应中的 ID，不写死主键。

```bash
python3 scripts/week05/verify.py api
```

## 数据持久化验证

先记录创建的活动和报名，再执行 docker compose down、docker compose up -d。等待应用恢复后按同一 ID 查询并比较完整响应，确保 ID、名称、时间、容量和报名记录保持一致。down 不带 -v。

```bash
python3 scripts/week05/verify.py persist
```

命名卷的生命周期独立于容器，因此删除并重建容器后 MySQL 数据仍被保留。已有卷不会再次执行初始化脚本，修改 .env 也不会自动更改已有数据库密码。

## 本机执行结果

<!-- WEEK05-RESULT-START -->
验证时间：2026-10-20 09:04:01 +0800。

- 多阶段镜像 scy-boardgame:week05 构建成功；构建阶段执行 Maven 测试。
- app 和 mysql:8.4.11 启动成功，应用健康接口返回 HTTP 200 / UP。
- 创建活动 HTTP 201，按 ID 查询 HTTP 200；活动 ID：1，报名 ID：1。
- 已实际执行 docker compose down 和 docker compose up -d，重建后活动与报名完整响应一致。
- 实际终端输出：evidence/01-build.txt、02-containers.txt、03-api.txt、04-before.txt、05-after.txt。
- 截图由本人保存到 screenshots/，与上述实际输出对应。
<!-- WEEK05-RESULT-END -->

## 证据目录

真实终端日志保存到 evidence/。截图由本人使用 macOS 截图功能保存到 screenshots/：
01-build.png、02-containers.png、03-api.png、04-before.png、05-after.png。
