package com.library.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 统一API响应结构类
 * 用于封装所有接口的响应数据，包含状态码、消息和数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 响应状态码
     * 200: 成功
     * 400: 请求参数错误
     * 401: 未授权或密码错误
     * 403: 禁止访问
     * 404: 资源不存在
     * 405: 方法不允许
     * 500: 服务器内部错误
     */
    private int code;

    /**
     * 响应消息
     * JSON序列化时字段名映射为"msg"
     */
    @JsonProperty("msg")
    private String message;

    /**
     * 响应数据
     * 泛型参数T支持返回任意类型的数据
     */
    private T data;

    /**
     * 自定义错误码常量
     * 与HTTP状态码保持一致
     */
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_METHOD_NOT_ALLOWED = 405;
    public static final int CODE_INTERNAL_ERROR = 500;

    /**
     * 生成成功响应（默认消息"请求成功"）
     *
     * @param data 返回数据
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return success("请求成功", data);
    }

    /**
     * 生成成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data 返回数据
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(CODE_SUCCESS);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 生成错误响应（默认400错误码）
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, CODE_BAD_REQUEST);
    }

    /**
     * 生成错误响应（自定义错误码）
     *
     * @param message 错误消息
     * @param errorCode 自定义错误码
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(String message, int errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode);
        response.setMessage(message);
        return response;
    }

    /**
     * 生成错误响应（包含数据和自定义错误码）
     *
     * @param message 错误消息
     * @param data 错误数据
     * @param errorCode 自定义错误码
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(String message, T data, int errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 生成400错误响应 - 请求参数错误
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(message, CODE_BAD_REQUEST);
    }

    /**
     * 生成401错误响应 - 未授权
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, CODE_UNAUTHORIZED);
    }

    /**
     * 生成403错误响应 - 禁止访问
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(message, CODE_FORBIDDEN);
    }

    /**
     * 生成404错误响应 - 资源不存在
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, CODE_NOT_FOUND);
    }

    /**
     * 生成500错误响应 - 服务器内部错误
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(message, CODE_INTERNAL_ERROR);
    }

    /**
     * 生成405错误响应 - 方法不允许
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> methodNotAllowed(String message) {
        return error(message, CODE_METHOD_NOT_ALLOWED);
    }
}