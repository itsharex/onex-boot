# OneX
![img](./_media/icon.svg ':size=120x120')

> OneX致力于提供一个开箱即用的基础功能模块，减轻开发人员的项目初始化和常见功能模块的开发工作。
> 
> 无意搭建一个低代码平台或承载大并发大数据量的产品，只想为陷于各种日常繁琐工作的朋友们减少点工作量。

## 版本说明
* [v4] 支持SpringBoot3, 要求JAVA17+
* [v3] 支持SpringBoot2, 要求JAVA1.8+
* 更多v3与v4区别及升级方式见[v3升级v4](boot/v3_to_v4.md),更详细版本区别见[更新记录]((boot/CHANGELOG.md))

## 使用方式
见[Setup](boot/Setup.md)

## 模块说明
- common 基础模块
  - 包含基础的注解、切片、过滤器、授权、工具类等，具体见[基础模块](boot/common.md)
- uc 用户中心(user center)模块
  - 包含用户、角色、菜单权限，以及基于RBAC的权限控制，具体见[用户中心](boot/uc.md)
- sys 系统模块
  - 包含字典、日志、存储、区域等，具体见[系统模块](boot/sys.md)
- msg 消息模块
  - 包含消息模块和消息记录。具体见[消息](boot/msg.md)
- job 定时任务模块
  - 包含定时任务的配置和执行等操作。具体见[定时任务](boot/job.md)
- tunnel 隧道模块
  - 包含网络、数据库、系统信息等隧道穿透。具体见[隧道](boot/tunnel.md)
- websocket Websocket模块
  - 包含基于Websocket的客户端与服务器交互机制。具体见[隧道](boot/tunnel.md)
- coder 代码生成器模块
  - 包含通过前后端的代码生成工具。具体见[代码生成器](boot/coder.md)

## 功能特色
todo

## 安全设计
系统设计以等保二级作为标准，具体细节见[安全保护](boot/security.md)

## 规范约束

### 数据库设计规范
对业务表的规范约束。具体见[数据库设计规范和实现](boot/standard_db.md)

### 接口设计规范
对接口和前后端交互第规范约束，具体见[接口设计规范](boot/standard_api.md)

### 代码规范
代码规范基本遵守[阿里巴巴Java开发手册](https://github.com/alibaba/p3c), 其它的代码规范约束，具体见[代码结构规范](boot/standard_code.md)


