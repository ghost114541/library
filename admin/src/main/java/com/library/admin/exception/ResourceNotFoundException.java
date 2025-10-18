package com.library.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 资源未找到异常类
 * 当查询或操作某个不存在的资源时抛出此异常
 * 使用@ResponseStatus注解标记返回HTTP状态码为404(NOT_FOUND)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // 序列化版本UID，用于版本控制
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数 - 使用自定义错误消息
     * @param message 自定义的错误描述信息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 构造函数 - 生成格式化的错误消息
     * @param resourceName 资源名称（如：用户、图书等）
     * @param fieldName 字段名称（如：ID、用户名等）
     * @param fieldValue 字段值（查询使用的具体值）
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}