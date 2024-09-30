# 基于 Spring Boot 3 + Vue 3 的前后端分离项目

## 项目简介

这是一个基于 **Spring Boot 3** 和 **Vue 3** 的前后端分离项目，支持二次开发，旨在提供一个简洁易用的开发框架，快速搭建功能丰富的Web应用。项目主要实现了用户管理、权限控制和基本的消息交互功能，适合用作学习和扩展。

## 项目仓库

[GitHub 仓库](https://github.com/NadeshikoToRin/study-project)

## 框架简介

- **Spring Boot + MyBatis**: 
  Spring Boot 简化了 Spring 应用的开发流程，MyBatis 提供了直观的 CRUD 操作支持。  
  [了解更多](https://spring.io/projects/spring-boot)

- **Redis**: 
  Redis 是一个基于内存的键值存储数据库，适用于高效的缓存和数据存取需求。  
  [Redis 官网](https://redis.io/)

## 后端项目结构

```
├─src                              // 源代码目录
│  ├─main                          // 主程序代码
│  │  ├─java                       // Java 源代码目录
│  │  │  └─com
│  │  │      └─example
│  │  │          └─studyprojectbackend    
│  │  │              ├─common      // 通用功能模块
│  │  │              ├─config      // 配置类模块
│  │  │              │  └─SecurityConfiguration // 安全配置类
│  │  │              ├─controller  // 控制层
│  │  │              │  └─AuthorizeController // 处理 API 相关请求
│  │  │              ├─mapper      // 数据访问层
│  │  │              │  └─UserMapper // 与用户信息表交互
│  │  │              ├─entity      // 实体类
│  │  │              │  ├─Account           // 用户实体类
│  │  │              │  ├─RestBean          // 前后端交互对象
│  │  │              ├─service     // 服务层接口
│  │  │              │  └─impl      // 服务实现类
│  │  │              │	  └─AuthorizeServiceImpl // 处理验证逻辑
│  │  │              │  ├─AuthorizeService // 验证相关接口
│  │  │              └─utils       // 工具类
│  │  └─resources                    // 资源文件
│  └─test                            // 测试代码目录
│      └─java                        // Java 测试代码
│          └─com
│              └─example
│                  └─studyprojectbackend          
└─target                             // 编译输出目录
```

## 前端项目结构

```
└─src                              // 源代码目录
    ├─assets                       // 静态资源
    ├─components                   // 组件
    │  └─welcome                   // 欢迎页面下的各个子组件
    │  	 └─ForgetPage.vue          // 忘记密码页面   
    │  	 └─LoginPage.vue           // 登录页面
    │  	 └─RegisterPage.vue        // 注册页面
    ├─net              			  // 封装请求方法模块
    ├─router                       // 路由配置
    ├─stores                       // 状态管理，组件间通信
    │  └─app                       
    ├─views                        // 视图文件
    │  └─Welcome.vue               // 欢迎页面,登录、注册、忘记密码的父视图
    │  └─IndexVue.vue              // 登录成功显示页面
    └─utils                        // 工具类函数
```

## 功能介绍

本项目提供基本的用户管理功能，包括但不限于：

### 1. 登录功能

- **实现原理**:
  - 用户通过输入用户名或邮箱和密码进行登录。
  - 后端使用 BCryptPasswordEncoder 加密存储的密码与用户输入的密码进行比较。
  - 登录成功后，生成 JWT（JSON Web Token）并返回，客户端存储该 token 以便后续请求中使用。
  - 前端使用 Vue Router 控制路由，确保用户必须登录才能访问受保护的页面。

### 2. 注册功能

- **实现原理**:
  - 用户在前端输入注册信息，前端通过 Element UI 对输入内容进行实时验证。
  - 前端向后端发送 POST 请求，后端通过正则表达式和数据库查重逻辑验证用户名和邮箱的唯一性。
  - 如果验证通过，后端会将用户信息保存到数据库，并向用户的邮箱发送验证邮件。

### 3. 重置密码

- **实现原理**:
  - 用户请求重置密码时，后端会生成并发送一个唯一的验证码到用户注册的邮箱。
  - 用户输入新密码和验证码后，后端验证验证码的有效性，并更新数据库中的用户密码。
  - 密码存储时同样使用 BCryptPasswordEncoder 进行加密，确保安全性。

### 4. 记住登录状态功能

- **实现原理**:
  - 使用 `persistent_logins` 表存储用户的登录状态信息。
  - 当用户选择“记住我”时，后端生成 token 并保存，设置有效期。
  - 每次用户访问应用时，后端检查 token 是否有效，若有效则自动登录用户。

## 数据库结构

### `db_account` 表

| 列名       | 数据类型     | 描述                              |
| ---------- | ------------ | --------------------------------- |
| `id`       | INT(20)      | 用户ID，主键                      |
| `username` | VARCHAR(255) | 用户名，UNIQUE                    |
| `password` | VARCHAR(255) | 加密后的密码                      |
| `email`    | VARCHAR(255) | 邮箱，UNIQUE                      |

### `persistent_logins` 表

| 列名        | 描述     | 数据类型    |
| ----------- | -------- | :---------- |
| `username`  | not null | VARCHAR(64) |
| `series`    | 主键     | VARCHAR(64) |
| `token`     | not null | VARCHAR(64) |
| `last_used` | not null | TIMESTAMP   |

## 开发规范

详细的开发规范请参见以下链接：

- [前端开发规范](https://developer.aliyun.com/article/850913#:~:text=1.5.1%20命名.)
- [后端开发规范](https://developer.aliyun.com/article/1327183)
- [API 命名规范](https://developer.aliyun.com/article/1352795#:~:text=OpenAPI%20规范)

---

## 效果图：

#### 登录页面：

![登录页面](preview-image\login.png)



#### 注册页面：

#### ![注册页面（用户名和密码不能与数据库中内容重复）](preview-image/register.png)



#### 忘记密码：

#### ![忘记密码第一步](preview-image/forget_step1.png)



#### 忘记密码：

#### ![忘记密码第一步](F:/findJob_2024/study-project/preview-image/forget_step2.png)