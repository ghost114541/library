package com.library.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 资源已存在异常类
 * 当尝试创建或更新资源时，如果资源已存在则抛出此异常
 * 使用@ResponseStatus注解标记返回HTTP状态码为409(CONFLICT)
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {

    // 序列化版本UID
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数 - 使用简单错误消息
     * @param message 错误描述信息
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * 构造函数 - 生成格式化的错误消息
     * @param resourceName 资源名称（如：用户、图书等）
     * @param fieldName 字段名称（如：用户名、ISBN等）
     * @param fieldValue 字段值（导致冲突的具体值）
     */
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}