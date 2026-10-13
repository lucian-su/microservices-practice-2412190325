# 数据库设计

数据库：MySQL 8，库名 boardgame_club，字符集 utf8mb4。初始化脚本：`monolith/scripts/init.sql`。默认使用 InnoDB，应用启动时通过 Hibernate validate 检查表结构，不自动修改表。

## activities：桌游活动

| 字段 | 类型 | 约束及含义 |
| --- | --- | --- |
| id | BIGINT | 自增主键 |
| name | VARCHAR(100) | 非空，活动名称 |
| location | VARCHAR(120) | 非空，活动地点 |
| starts_at | DATETIME(6) | 非空，活动开始时间，按本地时间使用 |
| capacity | INT | 非空，CHECK 限定 1～200 人 |

## registrations：活动报名

| 字段 | 类型 | 约束及含义 |
| --- | --- | --- |
| id | BIGINT | 自增主键 |
| activity_id | BIGINT | 非空，外键引用 activities.id |
| member_no | VARCHAR(30) | 非空，学号或社员编号 |
| member_name | VARCHAR(50) | 非空，成员姓名 |
| registered_at | DATETIME(6) | 非空，报名时间 |

关系：Activity 1:N Registration。`UNIQUE(activity_id, member_no)` 禁止重复报名；该索引也支持按活动查询与计数。外键不配置级联删除，避免删除有报名记录的活动。member_no 仅允许字母、数字、下划线和短横线，区分大小写；库使用 utf8mb4_0900_as_cs 排序规则。

## courses：课内实践示例

id 自增主键；code VARCHAR(30) 非空且唯一；name VARCHAR(100) 非空。与桌游业务独立，供第 11 题演示课程模型与 JPA 读写。初始化脚本通过幂等 INSERT 准备 MS04 课程。

## 数据访问与事务

数据访问位于 `com.zjgsu.scy.monolith.repository`；实体位于 model；请求及响应使用 DTO。Controller 仅调用 Service。报名在 READ_COMMITTED 事务中对活动行申请 PESSIMISTIC_WRITE 锁，随后进行存在性、重复、时间及容量检查，成功后保存并提交。

## 可重复测试数据

自动测试使用 H2 的 MySQL 模式及 create-drop，不要求本机 MySQL。每个测试自行创建实体；事务测试完成后回滚，不依赖测试顺序或遗留数据。MySQL 初始化脚本使用 IF NOT EXISTS，不清空已有业务数据；冒烟脚本每次创建新的活动并使用返回 ID。
