# How to Set Up

## 后端接口

## 技术准备
在使用前，建议对一些框架内使用到到的技术做一定了解，如：
* [SpringBoot](https://spring.io/projects/spring-boot/) 
* [MyBatis-Plus](https://mybatis.plus/)    
* [Shiro](http://shiro.apache.org/)    

## 开发准备
1. v3版本要求JDKL1.8+，v4版本求JDL17+。其中v3与v4之间区别以及升级方式见[v3升级v4](boot/v3_to_v4.md)
2. 建议使用[Intellij IDEA](https://www.jetbrains.com/idea/)开发

## onex使用姿势
对于在实际项目中使用onex，建议使用以下两种方式。
### 1. maven依赖(*推荐* )
在自己的项目中将onex中需要的模块用maven依赖加进来，[onex-api](https://github.com/zhangchaoxu/onex-api)是一个实践demo，适用场景：onex的基础功能能基本满足需求  
1.1 优点：可保持业务项目代码工程的简洁性，后续onex有升级或者bugfix，直接修改依赖的版本号即可。  
1.2 缺点：若onex中的基础功能与业务需求有冲突，需要拆包重写接口或者服务。

### 2. 复制工程
直接将onex作为自己的项目工程，在onex的代码工程基础上，添加自己的admin-api、app-api等业务模块，并暴露Application作为接口或者其他工程。
2.1 优点：包括onex在内所有代码都在自己的代码工程中，有任务业务需求都可以直接修改代码。    
2.2 缺点：无法简便的享受onex的更新，有需要的更新需要手动将需要的代码变动合并到代码工程中。

## 开发步骤,以maven依赖方式举例
1. 检出代码
```shell
### 地址是你自己的工程地址
git clone https://github.com/zhangchaoxu/onex-api.git
```
2. 使用IDEA打开检出代码文件夹
3. 创建MySQL数据库,并导入初始数据
4. 修改onex-boot\api\src\main\resources\application-dev.yml中数据库的地址、帐号和密码
5. 运行onex-boot\api\src\main\java\com\nb6868\onexboot\api\ApiApplication.java即可启动接口
6. 访问`http://127.0.0.1:18181/onex-boot-api`即可得到api success的提示
7. 项目集成了Swagger(knife4j),访问`http://127.0.0.1:18181/onex-boot-api/doc.html`即可访问和调试接口
8. 上述接口中端口18080和路径onex-boot-api,在onex-boot\api\src\main\resources\application.yml中配置

## 模块说明
- common 基础模块，包含基础的注解、切片、过滤器、授权、工具类等
- uc 用户中心(user center)模块，包含用户、角色、菜单权限，以及基于RBAC的权限控制，具体见[用户中心](boot/uc.md)
- sys 系统模块，包含字典、日志、存储、区域等，具体见[系统模块](boot/sys.md)
- msg 消息模块，包含消息模块和消息记录，消息指的是短信、钉钉通知等系统发送给用户的消息。具体见[消息](boot/msg.md)
- job 定时任务模块，包含定时任务的配置和执行等操作。具体见[定时任务](boot/job.md)
- tunnel 隧道模块，包含网络、数据库、系统信息等隧道穿透。具体见[隧道](boot/tunnel.md)
- websocket Websocket模块，包含基于Websocket的客户端与服务器交互机制。具体见[隧道](boot/tunnel.md)
- coder 代码生成器模块，包含通过前后端的代码生成工具。具体见[代码生成器](boot/coder.md)

### JAVA开发规范
详见[阿里巴巴Java开发手册](https://github.com/alibaba/p3c)

