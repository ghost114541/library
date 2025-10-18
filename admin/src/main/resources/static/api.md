# 图书馆管理系统API文档

## API概述

此文档提供了图书馆管理系统的REST API详细说明。

## 认证与授权

所有API请求需要进行身份验证。认证通过JWT令牌完成，令牌需要在HTTP请求头中提供：

```
Authorization: Bearer {token}
```

## 通用响应格式

所有API响应遵循以下JSON格式：

```json
{
  "code": 200,       // 状态码
  "message": "成功",  // 响应消息
  "data": {}         // 响应数据
}
```

## 错误码

| 错误码 | 描述 |
|--------|------|
| 200    | 成功 |
| 400    | 请求参数错误 |
| 401    | 未授权或授权失败 |
| 403    | 权限不足 |
| 404    | 资源不存在 |
| 500    | 服务器内部错误 |

## API端点

### 用户管理

#### 用户登录

- **URL**: `/api/auth/login`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": 1,
        "username": "admin",
        "role": "ADMIN"
      }
    }
  }
  ```

### 图书管理

#### 获取图书列表

- **URL**: `/api/books`
- **方法**: `GET`
- **参数**:
  - `page`: 页码（默认0）
  - `size`: 每页数量（默认10）
  - `sort`: 排序字段（可选）
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "content": [
        {
          "id": 1,
          "title": "Java编程思想",
          "author": "Bruce Eckel",
          "isbn": "9787111213826",
          "publisher": "机械工业出版社",
          "publicationDate": "2007-06-01",
          "category": "计算机",
          "status": "AVAILABLE"
        }
      ],
      "totalElements": 100,
      "totalPages": 10,
      "size": 10,
      "number": 0
    }
  }
  ```

#### 获取图书详情

- **URL**: `/api/books/{id}`
- **方法**: `GET`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "id": 1,
      "title": "Java编程思想",
      "author": "Bruce Eckel",
      "isbn": "9787111213826",
      "publisher": "机械工业出版社",
      "publicationDate": "2007-06-01",
      "category": "计算机",
      "status": "AVAILABLE",
      "description": "本书是Java程序员的必读经典，全面介绍了Java编程的各个方面。"
    }
  }
  ```

### 借阅管理

#### 借阅图书

- **URL**: `/api/borrows`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "userId": 1,
    "bookId": 1,
    "borrowDate": "2023-05-01",
    "returnDate": "2023-05-15"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "借阅成功",
    "data": {
      "id": 1,
      "userId": 1,
      "bookId": 1,
      "borrowDate": "2023-05-01",
      "returnDate": "2023-05-15",
      "status": "BORROWED"
    }
  }
  ```

## 更多API

更多API将在后续版本中提供。 