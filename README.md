# Twilight Blog - 后端

> 📌 **AI 代理请注意**：本项目的学习与优化进度记录在 [学习进度.md](学习进度.md)。
> 继续协助本项目前请先读该文件，了解当前进度、协作方式（只讲解、不代改代码）与用户的节奏要求。

> 基于 Spring Boot 4.1.0 的个人技术博客后端 API 服务

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![MySQL](https://img.shields.io/badge/MySQL-9.7-blue)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📋 目录

- [技术栈](#技术栈)
- [功能列表](#功能列表)
- [快速开始](#快速开始)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [部署](#部署)
- [许可证](#许可证)

---

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 (LTS) | 运行环境 |
| Spring Boot | 4.1.0 | 主框架 |
| Spring Data JPA | 4.1.0 | ORM 框架 |
| Spring Security | 7.1.0 | 密码加密（BCrypt） |
| JJWT | 0.12.5 | JWT Token 生成与解析 |
| MySQL | 9.7 | 数据库 |
| Maven | 3.9+ | 构建工具 |

---

## ✨ 功能列表

### 用户模块
- [x] 用户注册（BCrypt 密码加密）
- [x] 用户登录（JWT Token 返回）
- [x] Token 自动续期（24小时有效期）

### 文章模块
- [x] 发布文章（需认证）
- [x] 编辑文章（需认证，仅作者）
- [x] 删除文章（需认证，仅作者）
- [x] 文章列表（公开，按时间倒序）
- [x] 文章详情（公开，自动增加浏览量）

### 评论模块
- [x] 发表评论（需认证）
- [x] 查看评论列表（公开）
- [x] 删除评论（需认证，仅博主或作者）

### 安全模块
- [x] JWT 拦截器统一认证
- [x] 401 统一错误处理
- [x] 跨域配置（CORS）
- [x] 数据库密码环境变量管理

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/Twilight-tide/blog.git
cd blog
```

### 2. 创建数据库

```sql
CREATE DATABASE blog_db CHARACTER SET utf8mb4;
```

### 3. 配置环境变量

```bash
# Windows (PowerShell)
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="你的数据库密码"

# Linux / Mac
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=你的数据库密码
```

或在 IDEA 中配置：

```
Run → Edit Configurations → Environment variables:
MYSQL_USERNAME=root;MYSQL_PASSWORD=你的数据库密码
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080/api/test/ping`

---

## 📖 API 文档

### 统一返回格式

所有接口返回统一格式：

```json
{
    "code": 200,
    "msg": "success",
    "data": { ... }
}
```

| 字段 | 说明 |
|------|------|
| `code` | 状态码（200=成功，500=业务错误，401=未认证） |
| `msg` | 提示信息 |
| `data` | 返回数据 |

---

### 用户接口

#### 注册

```
POST /api/user/register
```

**请求体：**
```json
{
    "username": "twilight",
    "password": "123456",
    "nickname": "晓升汐落",
    "email": "twilight@blog.com"
}
```

**响应：**
```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 1,
        "username": "twilight",
        "nickname": "晓升汐落",
        "email": "twilight@blog.com",
        "createTime": "2026-07-21T18:46:50"
    }
}
```

#### 登录

```
POST /api/user/login
```

**请求体：**
```json
{
    "username": "twilight",
    "password": "123456"
}
```

**响应：**
```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "username": "twilight",
        "nickname": "晓升汐落"
    }
}
```

---

### 文章接口

#### 文章列表（公开）

```
GET /api/article/list
```

#### 文章详情（公开）

```
GET /api/article/{id}
```

#### 发布文章（需认证）

```
POST /api/article/publish
Authorization: Bearer {token}
```

**请求体：**
```json
{
    "title": "文章标题",
    "content": "文章内容（支持Markdown）",
    "summary": "文章摘要",
    "category": "技术分享"
}
```

#### 更新文章（需认证，仅作者）

```
PUT /api/article/{id}
Authorization: Bearer {token}
```

#### 删除文章（需认证，仅作者）

```
DELETE /api/article/{id}
Authorization: Bearer {token}
```

---

### 评论接口

#### 评论列表（公开）

```
GET /api/comment/list/{articleId}
```

#### 发表评论（需认证）

```
POST /api/comment/publish
Authorization: Bearer {token}
```

**请求体：**
```json
{
    "articleId": 1,
    "content": "评论内容",
    "parentId": 0
}
```

#### 删除评论（需认证，仅博主或作者）

```
DELETE /api/comment/{id}
Authorization: Bearer {token}
```

---

## 📁 项目结构

```
src/main/java/com/twilight/blog/
├── common/              # 通用组件
│   └── result/          # 统一返回结果 R
├── config/              # 配置类
│   ├── SecurityConfig   # Security 配置（跨域 + 放行）
│   └── WebConfig        # 拦截器注册
├── controller/          # 控制器
│   ├── ArticleController
│   ├── CommentController
│   ├── TestController
│   └── UserController
├── entity/              # 实体类
│   ├── BaseEntity       # 公共父类（ID + 时间）
│   ├── Article
│   ├── Comment
│   └── User
├── interceptor/         # 拦截器
│   └── JwtInterceptor   # JWT 认证拦截器
├── repository/          # JPA 数据访问层
│   ├── ArticleRepository
│   ├── CommentRepository
│   └── UserRepository
├── service/             # 业务逻辑层
│   ├── UserService
│   └── impl/
│       └── UserServiceImpl
└── utils/               # 工具类
    └── JwtUtil          # JWT 生成与解析
```

---


## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

## 📄 许可证

MIT License

---

## 🔗 相关链接

- 前端项目：[Twilight-tide/blog-frontend](https://github.com/Twilight-tide/blog-frontend)
- 在线 Demo：待部署