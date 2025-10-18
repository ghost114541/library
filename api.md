# 图书管理系统 API 文档

## 基础信息

- 基础URL: `http://localhost:8080`
- 所有响应格式统一为：
```json
{
  "code": 200,      // 状态码，200表示成功，非200表示错误
  "msg": "请求成功", // 响应消息
  "data": {}        // 响应数据，具体结构因接口而异
}
```

## 用户 API

### 1. 用户注册

- **URL**: `/api/users/register`
- **方法**: `POST`
- **描述**: 新用户注册
- **请求体**:
```json
{
  "username": "user123",
  "password": "password123",
  "email": "user@example.com",
  "phone": "13800138000"
}
```
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "userId": 1,
    "username": "user123",
    "email": "user@example.com",
    "phone": "13800138000",
    "role": "ROLE_USER"
  }
}
```
- **错误响应**:
```json
{
  "code": 409,
  "msg": "用户名已存在",
  "data": null
}
```

### 2. 用户登录

- **URL**: `/api/users/login`
- **方法**: `POST`
- **描述**: 用户登录并获取JWT令牌
- **请求体**:
```json
{
  "username": "user123",
  "password": "password123"
}
```
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "userId": 1,
    "username": "user123",
    "role": "ROLE_USER"
  }
}
```
- **错误响应**:
```json
{
  "code": 401,
  "msg": "用户名或密码错误",
  "data": null
}
```

### 3. 修改密码

- **URL**: `/api/users/{userId}/password`
- **方法**: `PUT`
- **描述**: 修改用户密码
- **权限**: 需要token认证，只能修改自己的密码
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword123"
}
```
- **响应体**:
```json
{
  "code": 200,
  "msg": "密码修改成功",
  "data": null
}
```
- **错误响应**:
```json
{
  "code": 400,
  "msg": "当前密码不正确",
  "data": null
}
```

### 4. 获取当前用户信息

- **URL**: `/api/users/profile`
- **方法**: `GET`
- **描述**: 获取当前登录用户的信息
- **权限**: 需要token认证
- **请求头**: `Authorization: Bearer {token}`
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "userId": 1,
    "username": "user123",
    "email": "user@example.com",
    "phone": "13800138000",
    "role": "ROLE_USER",
    "createTime": "2023-06-15T10:30:00"
  }
}
```

## 图书 API

### 1. 获取图书列表

- **URL**: `/api/books`
- **方法**: `GET`
- **描述**: 获取图书列表，支持分页、排序和筛选
- **权限**: 公开访问
- **请求参数**:
  - `page`: 页码，从0开始，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，如`title,asc`或`author,desc`
  - `title`: (可选) 按标题模糊查询
  - `author`: (可选) 按作者模糊查询
  - `category`: (可选) 按分类精确查询
  - `status`: (可选) 按状态查询，可选值：AVAILABLE, UNAVAILABLE
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "content": [
      {
        "bookId": 1,
        "title": "Spring Boot实战",
        "author": "张三",
        "isbn": "9787111111111",
        "publisher": "机械工业出版社",
        "publishDate": "2023-01-01",
        "stock": 10,
        "category": "计算机",
        "tags": "Spring,Java",
        "description": "这是一本关于Spring Boot的书籍",
        "status": "AVAILABLE"
      },
      // ... 更多图书
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 10,
    "totalElements": 100,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### 2. 获取图书详情

- **URL**: `/api/books/{bookId}`
- **方法**: `GET`
- **描述**: 获取单本图书的详细信息
- **权限**: 公开访问
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "bookId": 1,
    "title": "Spring Boot实战",
    "author": "张三",
    "isbn": "9787111111111",
    "publisher": "机械工业出版社",
    "publishDate": "2023-01-01",
    "stock": 10,
    "category": "计算机",
    "tags": "Spring,Java",
    "description": "这是一本关于Spring Boot的书籍",
    "status": "AVAILABLE"
  }
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "图书不存在",
  "data": null
}
```

### 3. 添加图书

- **URL**: `/api/books`
- **方法**: `POST`
- **描述**: 管理员添加新图书
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "title": "Spring Boot实战",
  "author": "张三",
  "isbn": "9787111111111",
  "publisher": "机械工业出版社",
  "publishDate": "2023-01-01",
  "stock": 10,
  "category": "计算机",
  "tags": "Spring,Java",
  "description": "这是一本关于Spring Boot的书籍"
}
```
- **响应体**:
```json
{
  "code": 200,
  "msg": "图书添加成功",
  "data": {
    "bookId": 1,
    "title": "Spring Boot实战",
    "author": "张三",
    "isbn": "9787111111111",
    "publisher": "机械工业出版社",
    "publishDate": "2023-01-01",
    "stock": 10,
    "category": "计算机",
    "tags": "Spring,Java",
    "description": "这是一本关于Spring Boot的书籍",
    "status": "AVAILABLE"
  }
}
```
- **错误响应**:
```json
{
  "code": 409,
  "msg": "ISBN已存在",
  "data": null
}
```

### 4. 修改图书信息

- **URL**: `/api/books/{bookId}`
- **方法**: `PUT`
- **描述**: 管理员修改图书信息
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **请求体**: (与添加图书相同格式)
- **响应体**:
```json
{
  "code": 200,
  "msg": "图书更新成功",
  "data": {
    "bookId": 1,
    "title": "Spring Boot实战（第二版）",
    "author": "张三",
    "isbn": "9787111111111",
    "publisher": "机械工业出版社",
    "publishDate": "2023-03-01",
    "stock": 15,
    "category": "计算机",
    "tags": "Spring,Java",
    "description": "这是一本关于Spring Boot的书籍（更新版）",
    "status": "AVAILABLE"
  }
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "图书不存在",
  "data": null
}
```

### 5. 删除图书

- **URL**: `/api/books/{bookId}`
- **方法**: `DELETE`
- **描述**: 管理员删除图书
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **响应体**:
```json
{
  "code": 200,
  "msg": "图书删除成功",
  "data": null
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "图书不存在",
  "data": null
}
```

## 借阅记录 API

### 1. 借阅图书

- **URL**: `/api/borrow`
- **方法**: `POST`
- **描述**: 用户发起借阅请求
- **权限**: 需要用户登录
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "bookId": 1,
  "dueDate": "2023-07-01T00:00:00" // 可选，不提供则默认为借阅日期后14天
}
```
- **响应体**:
```json
{
  "code": 200,
  "msg": "借阅成功",
  "data": {
    "recordId": 1,
    "userId": 1,
    "username": "user123",
    "bookId": 1,
    "bookTitle": "Spring Boot实战",
    "borrowDate": "2023-06-15T10:30:00",
    "dueDate": "2023-07-01T00:00:00",
    "returnDate": null,
    "status": "BORROWED"
  }
}
```
- **错误响应**:
```json
{
  "code": 400,
  "msg": "图书库存不足",
  "data": null
}
```

### 2. 归还图书

- **URL**: `/api/borrow/{recordId}/return`
- **方法**: `PUT`
- **描述**: 用户归还图书
- **权限**: 需要用户登录，只能归还自己借的书或管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **响应体**:
```json
{
  "code": 200,
  "msg": "归还成功",
  "data": {
    "recordId": 1,
    "userId": 1,
    "username": "user123",
    "bookId": 1,
    "bookTitle": "Spring Boot实战",
    "borrowDate": "2023-06-15T10:30:00",
    "dueDate": "2023-07-01T00:00:00",
    "returnDate": "2023-06-20T15:20:00",
    "status": "RETURNED"
  }
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "借阅记录不存在",
  "data": null
}
```

### 3. 查询个人借阅记录

- **URL**: `/api/borrow/user`
- **方法**: `GET`
- **描述**: 查询当前用户的借阅记录
- **权限**: 需要用户登录
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
  - `page`: 页码，从0开始，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，如`borrowDate,desc`
  - `status`: (可选) 按状态筛选，可选值：BORROWED, RETURNED, OVERDUE
  - `overdue`: (可选) 是否过期，true/false
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "content": [
      {
        "recordId": 1,
        "userId": 1,
        "username": "user123",
        "bookId": 1,
        "bookTitle": "Spring Boot实战",
        "borrowDate": "2023-06-15T10:30:00",
        "dueDate": "2023-07-01T00:00:00",
        "returnDate": null,
        "status": "BORROWED"
      },
      // ... 更多记录
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 2,
    "totalElements": 15,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### 4. 管理员查询所有借阅记录

- **URL**: `/api/borrow/admin`
- **方法**: `GET`
- **描述**: 管理员查询所有用户的借阅记录
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
  - `page`: 页码，从0开始，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，如`borrowDate,desc`
  - `userId`: (可选) 按用户ID筛选
  - `bookId`: (可选) 按图书ID筛选
  - `status`: (可选) 按状态筛选，可选值：BORROWED, RETURNED, OVERDUE
  - `overdue`: (可选) 是否过期，true/false
- **响应体**: (格式与个人借阅记录相同)

## 管理员 API

### 1. 查询所有用户

- **URL**: `/api/admin/users`
- **方法**: `GET`
- **描述**: 管理员查询所有用户信息
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
  - `page`: 页码，从0开始，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，如`userId,asc`
- **响应体**:
```json
{
  "code": 200,
  "msg": "请求成功",
  "data": {
    "content": [
      {
        "userId": 1,
        "username": "user123",
        "email": "user@example.com",
        "phone": "13800138000",
        "role": "ROLE_USER",
        "createTime": "2023-06-01T10:30:00"
      },
      // ... 更多用户
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 3,
    "totalElements": 25,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### 2. 更新用户角色

- **URL**: `/api/admin/users/{userId}`
- **方法**: `PUT`
- **描述**: 管理员更新用户角色
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
  - `role`: 角色名称，可选值：ROLE_USER, ROLE_ADMIN
- **响应体**:
```json
{
  "code": 200,
  "msg": "用户角色更新成功",
  "data": {
    "userId": 1,
    "username": "user123",
    "email": "user@example.com",
    "phone": "13800138000",
    "role": "ROLE_ADMIN",
    "createTime": "2023-06-01T10:30:00"
  }
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "用户不存在",
  "data": null
}
```

### 3. 删除用户

- **URL**: `/api/admin/users/{userId}`
- **方法**: `DELETE`
- **描述**: 管理员删除用户
- **权限**: 需要管理员权限
- **请求头**: `Authorization: Bearer {token}`
- **响应体**:
```json
{
  "code": 200,
  "msg": "用户删除成功",
  "data": null
}
```
- **错误响应**:
```json
{
  "code": 404,
  "msg": "用户不存在",
  "data": null
}
```
