# 校园二手交易平台 API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)

面向高校学生的校园闲置交易 REST API，覆盖用户、商品、订单、收藏、消息、文件上传和管理后台等模块。

配套前端：[campus-market-web](https://github.com/zzt6901-sketch/campus-market-web)

## 功能模块

- 用户注册、登录、JWT 鉴权和个人资料管理
- 商品发布、编辑、搜索、分类、价格与成色筛选
- 订单创建、付款、发货、收货和取消状态流转
- 商品收藏、站内消息和图片上传
- 管理员用户、商品、订单管理与统计接口
- Knife4j / OpenAPI 接口文档

## 技术栈

| 类别 | 技术 |
|---|---|
| Web | Spring Boot 3.2.5 |
| 安全 | Spring Security、JWT |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.0 |
| 文档 | Knife4j 4.4.0 |
| 构建 | Maven 3.9+、Java 17+ |

## 快速开始

### 1. 准备环境

- Java 17 或更高版本
- Maven 3.9 或更高版本
- MySQL 8.0 或更高版本

### 2. 初始化数据库

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

脚本会创建 `campus_market` 数据库、7 张核心表、商品分类和虚构演示数据。

### 3. 设置环境变量

项目不会自动读取 `.env` 文件。请在 IDE 运行配置、Shell 或操作系统中设置变量。可参考 [.env.example](.env.example)。

| 变量 | 默认值 | 说明 |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL 地址 |
| `DB_PORT` | `3306` | MySQL 端口 |
| `DB_NAME` | `campus_market` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | 空 | 本地数据库密码 |
| `JWT_SECRET` | 本地开发占位值 | JWT 密钥，公开部署必须覆盖 |
| `JWT_EXPIRATION` | `604800000` | Token 有效期，单位为 ms |
| `UPLOAD_PATH` | `./uploads/` | 本地上传目录 |
| `SERVER_PORT` | `8080` | 服务端口 |

Windows PowerShell 示例：

```powershell
$env:DB_PASSWORD = "你的本地 MySQL 密码"
$env:JWT_SECRET = "请替换为足够长的随机字符串"
mvn spring-boot:run
```

Bash 示例：

```bash
export DB_PASSWORD='你的本地 MySQL 密码'
export JWT_SECRET='请替换为足够长的随机字符串'
mvn spring-boot:run
```

### 4. 访问服务

- API：<http://localhost:8080>
- Knife4j：<http://localhost:8080/doc.html>
- OpenAPI JSON：<http://localhost:8080/v3/api-docs>

## 演示账号

初始化脚本包含 localhost 专用的虚构账号。默认密码为 `123456`。

| 角色 | 用户名 |
|---|---|
| 管理员 | `admin` |
| 普通用户 | `zhangsan` |
| 普通用户 | `lisi` |

请勿将演示密码用于真实环境。

## 测试与构建

```bash
mvn test
mvn clean package
```

配置安全测试会检查仓库中是否残留旧服务器地址、固定生产密钥和服务器绝对路径。

## API 概览

| 模块 | 基础路径 | 主要能力 |
|---|---|---|
| 用户 | `/api/user` | 注册、登录、资料、密码 |
| 商品 | `/api/product` | 列表、详情、发布、编辑、下架、分类 |
| 订单 | `/api/order` | 创建、付款、发货、收货、取消 |
| 收藏 | `/api/favorite` | 添加、取消、查询 |
| 消息 | `/api/message` | 发送、列表、未读和已读 |
| 文件 | `/api/file` | 图片上传 |
| 管理 | `/api/admin` | 用户、商品、订单和统计管理 |

## 安全说明

- 仓库不提交真实数据库密码、JWT 密钥、服务器 IP 或上传文件。
- `application-prod.yml` 要求运行环境显式提供 `DB_PASSWORD` 和 `JWT_SECRET`。
- `.env`、本地配置、日志、上传目录和构建产物均已加入 `.gitignore`。
- 部署时应使用独立数据库账号、HTTPS、反向代理和高强度随机密钥。

## 项目结构

```text
src/main/java/com/campus/trading/
├── admin/       # 管理接口
├── common/      # 统一响应、异常和工具
├── config/      # Security、MyBatis、Swagger、Web 配置
├── modules/     # user、product、order、favorite、message、file
└── security/    # JWT 与认证过滤器
```

## 许可证

本项目用于学习、作品集和技术交流。第三方依赖遵循各自许可证。
