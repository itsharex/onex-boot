# 数据库设计规范和实现

程序开发以MySQL作为目标,目前在MySQL8.0中经过比较成熟的实践，在PostgreSQL中经过简单测试验证。 
_注意_ 修改表中各个数据库中有区别的地方 
_注意_ 修改Mapper文件中是否有MySQL特有的查询语句

## 数据持久化实现

数据库的持久化在比较JPA、Hibernate、MyBatis之后选择由[MyBatis-Plus](https://mybatis.plus/)实现,兼具易用性和灵活性。

## 表命名与字段设计规范

1. 数据库名、表名、字段名一律小写,用_分割
2. 遵守`模块名_业务名`的形式,比如`uc_user`
3. 关联表使用两个表名连接方式，比如`uc_user_role`
4. 建议所有表都带上id、deleted、create_time、update_time、create_id、update_id等标准字段
5. 多租户表都带上tenant_code
6. 表示时间的字段用xx_time(datetime),表示日期的字段用xx_date(date)
7. 默认使用InnoDB引擎
8. 默认字符集utf8mb4,默认排序utf8mb4_general_ci
9. 索引命名:
   * pk_表名缩写_字段名: 为兼容多数据库,索引必须全库唯一
   * 唯一索引命名: uk_表名缩写_字段名
   * 普通索引命令: idx_表名缩写_字段名

## 主键
~~
数据主键id使用MyBatisPlus的ID_Worker，一律使用20位的unsigned tinyint，注意在pojo中使用Long定义  
优点是兼具自增主键的连续性(优化查询效率)和uuid的不可推测性
~~
经过实际项目验证，最终还是决定使用自增主键。
注意\_ JavaScript无法处理Java的Long,会导致精度丢失,具体表现为主键最后两位永远为0,解决思路:Long 转为 String 返回 [更多文档](https://mybatis.plus/guide/logic-delete.html)

## 逻辑删除
~~
逻辑删除deleted使用1位的unsigned tinyint,1表示删除,0表示未删除  
在Entity中为deleted加上@TableLogic注解,对于使用Wrapper查询的sql会自动加上deleted = 1
~~
原项目中采用0和1作为逻辑删除的区分，后来发现以下几个问题不好处理
1. 无法直接获得删除时间，只能通过update_time的填充(而将删除时间填充如update_time也会有歧义)或者是日志记录查询。
2. 无法满足一些唯一约束，比如用户表的username必须唯一且有unique key的约束，如果出现(多次)删除后，就无法再次添加。   
为了解决上述问题，最终决定采用0和unix_timestamp作为逻辑删除的区分，逻辑删除字段还是使用deleted，数据类型限定为Long(int8)。    
默认值为0，表示位删除
当逻辑删除后塞入unix秒时间戳，对于上述问题2，可以使用username + deleted组合作为unique key约束。

需要注意的是：
但是如果在mapper中做了自定义级联查询,需要手动加上deleted = 0的条件  
[更多文档](https://mybatis.plus/guide/logic-delete.html)

## 自动填充

自动填充通过AutoFillMetaObjectHandler实现,实现方式是填充到入参的entity内  
_注意_ 对于update方法只有update\(entity, updateWrapper\)才会自动填充，直接调用update\(updateWrapper\)是不会自动填充的 [更多文档](https://mybatis.plus/guide/auto-fill-metainfo.html)

## 分页

使用MybatisPlus的数据分页实现 [更多文档](https://mybatis.plus/guide/page.html) 更多问题见[MyBatisPlus文档](https://mybatis.plus/)

## 字段业务冗余

对于关联表的字段,适当考虑冗余到表中,比如文章表中的文章分类名称,可以考虑冗余到文章表中,在文章分类修改的时候,统一修改一下文章中的冗余字段即可。

## 大小写敏感

数据库设计中一律采用小写表名和字段名,其中定时任务默认创建表为大写,建议将mysql设置为大小写不敏感。  
修改/etc/my.cnf文件,在[mysqld]节点加入配置`lower_case_table_names = 1`,然后重启mysql(`service mysqld restart`)即可

## json
json是mysql 5.7引入，然后在mysql 8.0得到优化的功能
对于开发，可以是Entity中定义为json，或者
```java
@TableName(value = "table_demo", autoResultMap = true)
public class DemoEntity extends BaseEntity {
   @TableField(typeHandler = JacksonTypeHandler.class)
   private JSONObject params;    
}
```

## postgresql建表语句
````postgresql
DROP TABLE IF EXISTS "test"."uc_test";
CREATE TABLE "test"."uc_test" (
  "id" int8 NOT NULL GENERATED ALWAYS AS IDENTITY,
  "create_name" varchar(50),
  "create_id" int8,
  "create_time" timestamp(6),
  "update_id" int8,
  "update_time" timestamp(6),
  "deleted" int8 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
)
;
COMMENT ON COLUMN "test"."uc_test"."id" IS 'ID';
COMMENT ON COLUMN "test"."uc_test"."create_name" IS '创建者名字';
COMMENT ON COLUMN "test"."uc_test"."create_id" IS '创建者ID';
COMMENT ON COLUMN "test"."uc_test"."create_time" IS '创建时间';
COMMENT ON COLUMN "test"."uc_test"."update_id" IS '更新者ID';
COMMENT ON COLUMN "test"."uc_test"."update_time" IS '更新时间';
COMMENT ON COLUMN "test"."uc_test"."deleted" IS '逻辑删除';
COMMENT ON TABLE "test"."uc_test" IS '模块-表名';
````

## ref

* [并发扣款，如何保证数据的一致性？](https://mp.weixin.qq.com/s?__biz=MjM5ODYxMDA5OQ==&mid=2651962738&idx=1&sn=d2d91a380bad06af9f7b9f7a80db26b3)
* [并发扣款一致性，幂等性问题，这个话题还没聊完！！！](https://mp.weixin.qq.com/s/xXju0y64KKUiD06QE0LoeA)
* [MyBatis-plus 从入门到入土](https://mp.weixin.qq.com/s/SBkYZrBbGEgBe09erNr7tg)

