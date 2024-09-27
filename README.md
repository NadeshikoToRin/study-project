## 基于SpringBoot3 + vue3的前后端分离项目

### 框架简介:<br>

- **Spring Boot + MyBatis**: Spring Boot 是用于简化 Spring 应用开发的框架，Mybatis 提供更符合直觉的CRUD操作  
  [了解更多](https://spring.io/projects/spring-boot)
- **Redis **:Redis 是一个基于内存的键值存储数据库。  
  [Redis 官网](https://redis.io/)

### 后端项目结构：

```
├─src                              // 源代码目录
│  ├─main                          // 主程序代码
│  │  ├─java                       // Java 源代码目录
│  │  │  └─com
│  │  │      └─example
│  │  │          └─studyprojectbackend    // 项目根包
│  │  │              ├─common      // 通用功能模块（工具类、常量等）
│  │  │              ├─config      // 配置类模块
│  │  │              │  └─SecurityConfiguration// 配置安全过滤链，包括请求授权、
│  │  │              │  						登录、登出、跨域和异常处理
│  │  │              ├─controller  // 处理 HTTP 请求的控制层
│  │  │              │  └─AuthorizeController// 处理api/auth相关的所有http请求
│  │  │              ├─mapper      // 数据访问层
│  │  │              │  └─UserMapper// 与db_account（用户信息）表进行交互
│  │  │              ├─entity       // 实体类，数据模型
│  │  │              │  ├─Account           // 用户实体类（与数据库表映射）
│  │  │              │  ├─RestBean          // 前后端交互对象
│  │  │              ├─service             // 服务层接口
│  │  │              │  └─impl             // 服务层实现类
│  │  │              │	  └─AuthorizeServiceImpl   // 服务层实现类，处理验证逻辑
│  │  │              │  ├─AuthorizeService// 验证相关的接口
│  │  │              └─utils               // 工具类（常用的功能或辅助方法）
│  │  └─resources                          // 资源文件（配置文件、静态资源等）
│  └─test                                  // 测试代码目录
│      └─java                              // Java 测试代码
│          └─com
│              └─example
│                  └─studyprojectbackend          
└─target                       // 编译输出目录
```
### 前端项目结构：

```
└─src                              // 项目的源代码目录
    ├─assets                       // 静态资源文件（如图片、字体等）
    ├─components                   // 组件
    │  ├─form                      // 表单组件（处理用户个人中心的三个表单组件）
    │  └─message                   // 消息组件（用于显示聊天或者通知页面元素）
    ├─requestMethod                // 请求方法模块（UI框架二次封装axios API ）
    ├─router                       // 路由配置（定义前端页面的路由，Vue Router）
    ├─stores                       // 状态管理（管理应用的全局状态，pinia）
    │  └─app                       
    ├─utils                        // 工具类函数（封装常用的工具方法，时间戳处理、数据请求体）
    └─views                        // 视图文件（页面组件，通常是路由对应的页面）
```

### 关于项目：

- 包含基本的登录、注册、密码重置等等功能，可以二次开发编写具体场景下的应用程序。
  
  - 登录功能（支持用户名、邮箱登录，包含记住我rememberMe）
  - 注册用户（通过邮箱注册）
  - 重置密码（通过邮箱重置密码）
  
  登录功能：
  
  1. 用户登录成之后，才能访问index路径下的页面
  2. 用户如果没有登录，那么会自动跳转到登录界面
  3. 如果用户请求的是一个压根就不存在的页面，依然强制回到登录界面，如果已经登录，那么回到index首页
  
  登录解决方案
  
  1. 无论是否已经登录，直接向后端请求用户信息
  2. 如果请求成功，那么说明肯定是已经登录了
  3. 如果请求失败，那么说明没有登录，跳转到登录界面
  
  
  
  注册功能
  
  1. 用户注册信息必须合法，所有用户信息均通过前端校验后才能发送register请求
  2. 



---

### study_db数据库结构：

### db_account表

| 列名       | 数据类型     | 描述                              |
| ---------- | ------------ | --------------------------------- |
| `id`       | INT(20)      | 用户ID，主键                      |
| `username` | VARCHAR(255) | 用户名，UNIQUE                    |
| `password` | VARCHAR(255) | BCryptPasswordEncoder加密后的密码 |
| `email`    | VARCHAR(255) | 邮箱，UNIQUE                      |



### persistent_logins表，用于实现rememberMe功能,**jdbcTokenRepository**创建

| 列名        | 描述     | 数据类型    |
| ----------- | -------- | :---------- |
| `username`  | not null | VARCHSR(64) |
| `series`    | 主键     | VARCHAR(64) |
| `token`     | not null | VARCHAR(64) |
| `lase_used` | not null | VARCHAR(64) |

------

### 项目开发规范详见 [[前后端分离的接口规范-阿里云开发者社区 (aliyun.com)](https://developer.aliyun.com/article/1005469#:~:text=往往前后端接口联调对))

+ [前端开发规范](https://developer.aliyun.com/article/850913#:~:text=1.5.1 命名.)
+ [后端开发规范](https://developer.aliyun.com/article/1327183)
+ [Api命名规范]([https://developer.aliyun.com/article/1352795#:~:text=OpenAPI 规范)