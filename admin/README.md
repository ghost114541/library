# 图书馆管理系统 - 后端 API

这是使用 Spring Boot 构建的图书馆管理系统的后端 API。

## 功能

- 使用 JWT 进行用户身份验证和授权
- 图书管理（添加、更新、删除、搜索）
- 借阅记录管理
- 管理员操作
- 错误处理和验证

## 技术栈
- Java 11
- Spring Boot 2.7.14
- Spring Security with JWT
- Spring Data JPA
- MySQL 8.0
- Maven

## API 端点

### 用户 API

- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `PUT /api/users/{userId}/password` - 更改密码
- `GET /api/users/profile` - 获取当前用户信息

### 图书 API

- `GET /api/books` - 获取书籍列表（支持分页、排序、过滤）
- `GET /api/books/{bookId}` - 获取书籍详细信息
- `POST /api/books` - 添加书籍（仅限管理员）
- `PUT /api/books/{bookId}` - 更新图书信息（仅限管理员）
- `DELETE /api/books/{bookId}` - 删除书籍（仅限管理员）

### 借阅记录 API

- `POST /api/borrow` - 创建借阅请求
- `PUT /api/borrow/{recordId}/return` - 还书
- `GET /api/borrow/user` - 获取用户借阅记录
- `GET /api/borrow/admin` - 获取所有借阅记录（仅限管理员，支持按状态、逾期等过滤）

### 管理 API

- `GET /api/admin/users` - 获取所有用户（仅限管理员）
- `PUT /api/admin/users/{userId}` - 更新用户角色（仅限管理员）
- `DELETE /api/admin/users/{userId}` - 删除用户（仅限管理员）

## 设置和安装

1. 确保已安装 JDK 11 和 Maven
2. 克隆代码库
3. 在 `application.properties` 中配置 MySQL 数据库
4. 运行应用程序
```
mvn spring-boot:run
```

## API 文档

该 API 返回标准化的 JSON 响应，其结构如下：

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2023-06-15T10:30:45",
  "status": 200
}
```

对于分页，API 支持以下标准参数：
- `page` - 页码（从 0 开始）
- `size` - 页面大小
- `sort` - 排序字段（例如 `sort=title,asc`）

## 安全性

- 通过 JWT 令牌进行身份验证
- 密码使用 BCrypt 加密
- API 强制基于角色的访问控制

## 许可证

本项目采用 MIT 许可证。