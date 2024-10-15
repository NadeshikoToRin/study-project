# 基于 Spring Boot 3 + Vue 3 的前后端分离项目

## 项目简介

这是一个基于 JDK 17、Spring Boot 3、Spring Security 6、Redis、Mybatis、Vue 3、Element-Plus 构建的前后端分离项目。项目主要实现了用户管理、权限控制、邮箱验证、验证码验证、用户注册、密码重置等功能，旨在提供一个简洁易用且具有良好可扩展性的开发框架，支持快速搭建功能丰富的 Web 应用。

## 项目仓库

[GitHub 仓库](https://github.com/NadeshikoToRin/study-project)

## 技术栈

- **后端**：Spring Boot 3、Spring Security 6、MyBatis、Redis、BCrypt 密码加密
- **前端**：Vue 3、Element-Plus、Pinia 状态管理、Vue Router、Vite
- **数据库**：MySQL、Redis
- **依赖管理**：Maven

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
│  │  │              │  ├─SecurityConfiguration // 安全配置类
│  │  │              │  └─WebConfiguration // 拦截器规则配置类，注册拦截器到拦截器链
│  │  │              ├─interceptor      // 拦截器
│  │  │              │  └─AuthorizeInterceptor // 将认证用户信息AccountUser存储到session中
│  │  │              ├─controller  // 控制层
│  │  │              │  ├─AuthorizeController // 处理 API 相关请求
│  │  │              │  └─UserController // 处理 API 相关请求
│  │  │              ├─mapper      // 数据访问层
│  │  │              │  └─UserMapper // 与用户信息表交互
│  │  │              ├─entity      // 实体类
│  │  │              │  ├─Account           // 用户实体类
│  │  │              │  ├─AccountUser       // 认证对象用户实体类
│  │  │              │  └─RestBean          // 前后端交互对象
│  │  │              ├─service     // 服务层接口
│  │  │              │  └─impl      // 服务实现类
│  │  │              │	  └─AuthorizeServiceImpl // 处理验证逻辑
│  │  │              │  ├─AuthorizeService // 验证相关接口
│  │  │              └─utils       // 工具类
└─ └─ └─resources                    // 资源文件
```

### 主要功能模块

- **AuthorizeController**：处理用户登录、注册、邮箱验证、密码重置等请求。
- **AuthorizeServiceImpl**：实现验证码的发送、校验及用户的注册、密码重置等业务逻辑。通过 Redis 存储验证码、BCrypt 加密用户密码。
- **UserMapper**：提供与数据库的交互接口，处理用户信息的查询、更新、删除等操作。
- **SecurityConfiguration**：使用 Spring Security 进行安全配置，支持 用户认证及权限控制。

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
    ├─router                       // 路由配置，配置路由守卫，用户必须登录才能访问IndexVue下的页面
    ├─stores                       // 状态管理，组件间通信                      
    ├─views                        // 视图文件
    │  └─Welcome.vue               // 欢迎页面,登录、注册、忘记密码的父视图
    │  └─IndexVue.vue              // 登录成功显示页面
    └─utils                        // 工具类函数
```

### 主要功能模块

- **LoginPage.vue**：处理用户登录页面。
- **RegisterPage.vue**：处理用户注册页面，集成邮箱验证功能。
- **ForgetPage.vue**：处理用户忘记密码页面，支持验证码验证及密码重置。
- **Vue Router**：路由管理，包含路由守卫，确保未登录用户无法访问受保护页面。
- **Pinia**：状态管理，管理用户登录信息和其他全局状态。

### 路由功能说明

- **欢迎页面** (`/`)：

  - 默认显示登录页面。
  - 包含子路由：注册页面(`/register`)、忘记密码页面(`/forget`)。

- **用户主页** (`/index`)：

  - 登录成功后重定向至该页面。

- **路由守卫**：

  - 如果用户已登录，访问欢迎页面将自动重定向至用户主页。
  - 如果用户未登录，访问用户主页将重定向至登录页面。

  

  ### 状态管理

  项目使用 Pinia 进行状态管理，提供全局的用户认证状态管理：

  ```javascript
  import { ref, computed, reactive } from 'vue';
  import { defineStore } from 'pinia';
  
  export const useStore = defineStore('store', () => {
      const auth = reactive({
          user: null
      });
  
      return { auth };
  });
  ```

  ### 状态管理功能说明

  - auth

    ：用于存储用户的登录状态信息。

    - `user`: 记录当前登录用户的信息，默认为 `null`。

  

## 功能介绍

本项目提供的主要功能包括用户管理、权限控制和安全验证，具体如下：

### 1. 登录功能

- **实现原理**:
  - 用户通过输入用户名或邮箱和密码进行登录。
  - 后端使用 BCryptPasswordEncoder 加密存储的密码与用户输入的密码进行比较。
  - 登录成功后，通过session.setAttribute生成Account对象并返回，客户端存储该 对象以便后续请求中使用。
  - 前端使用 Vue Router 控制路由，确保用户必须登录才能访问受保护的页面。

### 2. 注册功能

- **实现原理**:
  - 用户在前端输入注册信息，前端通过 Element UI 对输入内容进行实时验证。
  - 前端向后端发送 POST 请求，后端通过正则表达式和数据库查重逻辑验证用户名和邮箱的唯一性。
  - 如果验证通过，后端会将用户信息保存到数据库，并向用户的邮箱发送验证邮件。

### 3. 邮箱验证与验证码

- **实现原理**:
  - 在用户注册或找回密码时，系统生成一个 6 位随机验证码，通过 Redis 存储，并通过邮件发送到用户邮箱。
  - Redis 中的验证码有效期为 3 分钟，用户需在有效期内输入验证码。
  - 后端校验用户输入的验证码是否正确，验证通过后允许注册或重置密码。

### 4. 重置密码

- **实现原理**:
  - 用户请求重置密码时，后端生成并发送一个唯一的验证码到用户注册的邮箱。
  - 用户输入新密码和验证码后，后端验证验证码的有效性，并更新数据库中的用户密码。
  - 密码存储时同样使用 BCryptPasswordEncoder 进行加密，确保安全性。

### 5. 记住登录状态功能

- **实现原理**:
  - 使用 `persistent_logins` 表存储用户的登录状态信息。
  - 当用户选择“记住我”时，后端生成 tokenRepository(tokenRepository)并保存，设置有效期。
  - 每次用户访问应用时，后端检查 rememberMe是否有效，若有效则自动登录用户。

## 数据库结构

### `db_account` 表

| 列名       | 数据类型     | 描述           |
| ---------- | ------------ | -------------- |
| `id`       | INT(20)      | 用户ID，主键   |
| `username` | VARCHAR(255) | 用户名，UNIQUE |
| `password` | VARCHAR(255) | 加密后的密码   |
| `email`    | VARCHAR(255) | 邮箱，UNIQUE   |

### `persistent_logins` 表

| 列名        | 描述     | 数据类型    |
| ----------- | -------- | :---------- |
| `username`  | not null | VARCHAR(64) |
| `series`    | 主键     | VARCHAR(64) |
| `token`     | not null | VARCHAR(64) |
| `last_used` | not null | TIMESTAMP   |

## 开发规范

- **前端开发规范**: 请参见[前端开发规范](https://developer.aliyun.com/article/850913#:~:text=1.5.1%20命名.)
- **后端开发规范**: 请参见[后端开发规范](https://developer.aliyun.com/article/1327183)
- **API 命名规范**: 请参见[API 命名规范](https://developer.aliyun.com/article/1352795#:~:text=OpenAPI%20规范)

## 项目展示

### 登录页面：

![登录页面](preview-image/login.png)

### 注册页面：

![注册页面](preview-image/register.png)

### 忘记密码：

![忘记密码第一步](preview-image/forget_step1.png)

![忘记密码第二步](preview-image/forget_step2.png)

## 安装与运行

1. 克隆项目：

   ```bash
   git clone https://github.com/NadeshikoToRin/study-project.git
   ```

2. 进入项目目录：

   ```bash
   cd study-project
   ```

3. 启动后端服务：

   ```bash
   cd study-project-backend
   mvn spring-boot:run
   ```

4. 启动前端项目：

   ```bash
   cd study-project-frontend
   npm install
   npm run dev
   ```

5. 打开浏览器访问 `http://localhost:5173`（前端服务）或 `http://localhost:8080`（后端服务）。

## API 说明

| HTTP 方法 | API 路径                         | 描述                                     |
| --------- | -------------------------------- | ---------------------------------------- |
| POST      | `/api/auth/login`                | 用户登录                                 |
| GET       | `/api/auth/logout`               | 用户登出                                 |
| POST      | `/api/auth/valid-register-email` | 发送注册邮箱验证码                       |
| POST      | `/api/auth/valid-reset-email`    | 密码重置验证码                           |
| GET       | `/api/user/me`                   | 获取用户资料                             |
| POST      | `/api/auth/verify-saved`         | 注册用户时验证用户名或密码是否存在       |
| POST      | `/api/auth/register`             | 用户注册                                 |
| POST      | `/api/auth/start-rest`           | 重置密码第一步：验证邮箱和验证码         |
| POST      | `/api/auth/do-reset`             | 重置密码第二步：将修改后的密码存入数据库 |

## 结语

欢迎贡献和反馈！如果您在使用本项目时遇到问题或有建议，请在 GitHub 提交 Issue。