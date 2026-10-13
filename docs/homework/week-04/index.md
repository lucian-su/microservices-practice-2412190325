# Week 04：单体应用与数据库

姓名：施晨煜　学号：2412190325

仓库：https://github.com/lucian-su/microservices-practice-2412190325

继续使用第 03 周的 monolith/，基础包名 com.zjgsu.scy.monolith。参考收到的 Todo 分层示例，实现个人选题“桌游活动发布与报名”。

## 业务建模

核心流程：创建桌游活动 → 查看活动 → 输入成员编号与姓名报名 → 检查活动时间、重复报名与名额 → 写入数据库 → 查询报名记录。

Activity 与 Registration 为两个核心业务实体，关系为一对多；memberNo 标识成员，同一活动中不能重复报名。详细范围及关系见 ../../project-proposal.md。

验证命令：`cd monolith && python3 scripts/smoke-test.py`（应用及 MySQL 先启动）。验证活动创建后可查询，成功报名可查询到对应记录，第二次同成员报名及超出容量均返回 409。

## 单体应用实现

model / dto / repository / service / controller / exception 分包实现。Controller 处理路由、输入校验、HTTP 状态码和 Location；Service 实现业务规则及事务；Repository 使用 Spring Data JPA 访问数据库。六个活动报名接口均使用 /api/activities 前缀，另保留 /api/hello 和 /actuator/health。

运行命令：按根 README 初始化 MySQL 后，在 monolith/ 执行 `./mvnw spring-boot:run`。另一个终端执行 `python3 scripts/smoke-test.py`。

结果：创建活动与报名返回 201，查询返回 200，无效请求返回 400，活动不存在返回 404，重复报名及人数已满返回 409。

代码图片：screenshots/12-models.png、screenshots/12-service.png。
接口图片：screenshots/12-api.png。原始记录：evidence/api-mysql.log。

## 数据库持久化

MySQL 表结构和约束见 ../../database.md；初始化脚本为 monolith/scripts/init.sql。活动及报名通过外键关联，(activity_id, member_no) 唯一约束防止重复记录。应用使用 DTO 承载输入和输出，不直接暴露 JPA 实体。

准备命令：仓库根目录执行 `mysql -u root < monolith/scripts/init.sql`，按 README 创建本地应用账号并配置 DB_URL、DB_USERNAME、DB_PASSWORD。脚本可重复执行，不删除业务数据。

验证命令：`mysql -u root boardgame_club -e "SELECT * FROM activities; SELECT * FROM registrations;"`；重启应用，再查询报名列表。实际数据库读写和重启验证结果见 evidence/mysql-read-write.log、evidence/restart-persistence.log。

证据图片：screenshots/12-repository.png、screenshots/12-database.png。

## 测试与验证

在 monolith/ 执行 `./mvnw test`。测试使用 H2 MySQL 模式、create-drop；测试方法自行准备数据，事务测试回滚，独立并发测试在 finally 中删除自己的记录。测试顺序不共享业务状态。

- ActivityRepositoryTest：数据库读写、关联查询、唯一约束、课程模型读写。
- ActivityServiceTest：成功报名、重复报名、人数已满、已开始活动、活动不存在。
- ActivityApiTest：完整接口流程、Location、200/201/400/404/409、课程查询。
- RegistrationConcurrencyTest：两个独立事务竞争最后一个名额，只允许一个报名成功。
- MonolithApplicationTests：保留第 03 周启动测试。

实际运行两轮，均为 15 个测试，Failures 0、Errors 0、Skipped 0；MySQL 8.0.46 API 冒烟与重启持久化验证通过。最终测试数量和结果见 evidence/tests-summary.log、evidence/tests-first.log、evidence/tests-repeat.log。图片：screenshots/12-tests.png。

## 课内第 11 题材料

另外实现 Course 模型、CourseRepository、CourseService、CourseController，在同一应用中提供 GET /api/courses；数据库脚本准备 MS04 课程，Repository 测试验证保存后重新读取。

代码图片：screenshots/11-code.png。运行图片：screenshots/11-run.png。座位号按本人实际课堂情况填写。

## 证据说明

代码图片由源码展示页截图；运行图片由真实执行日志展示页截图。展示页保存在 evidence/*.html，原始日志同时保留，便于核对。图片来自本次工作环境，不代表某台 Mac 的桌面或课堂出勤。运行截图不使用同学提供的运行结果。
