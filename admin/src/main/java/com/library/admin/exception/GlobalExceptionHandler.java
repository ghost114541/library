package com.library.admin.exception;

import com.library.admin.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理类，使用@ControllerAdvice注解处理控制器抛出的各种异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.notFound(ex.getMessage()));
    }

    /**
     * 处理资源已存在异常
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        logger.error("Resource already exists: {}", ex.getMessage());
        // 对于冲突，使用400错误码
        return ResponseEntity.ok(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    /**
     * 处理凭证错误异常（错误的用户名或密码）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.unauthorized("Invalid username or password"));
    }

    /**
     * 处理访问被拒绝异常（权限不足）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.forbidden("Access denied, insufficient permissions"));
    }

    /**
     * 处理方法参数验证失败异常（@Validated或@Valid注解触发的验证错误）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.error("Validation failed", errors, ApiResponse.CODE_BAD_REQUEST));
    }

    /**
     * 处理绑定异常（表单提交参数绑定错误）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(BindException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.error("Validation failed", errors, ApiResponse.CODE_BAD_REQUEST));
    }

    /**
     * 处理约束违反异常（方法参数级验证错误）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        logger.error("Constraint violation: {}", errors);
        return ResponseEntity.ok(ApiResponse.error("Parameter validation failed", errors, ApiResponse.CODE_BAD_REQUEST));
    }

    /**
     * 处理HTTP消息不可读异常（请求体解析失败）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Message not readable: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.badRequest("Invalid or missing request body"));
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch: {}", ex.getMessage());
        String message = String.format("Parameter type error: '%s' should be of type %s", ex.getName(), ex.getRequiredType().getSimpleName());
        return ResponseEntity.ok(ApiResponse.badRequest(message));
    }

    /**
     * 处理缺少Servlet请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        logger.error("Missing parameter: {}", ex.getMessage());
        String message = String.format("Missing required parameter: '%s'", ex.getParameterName());
        return ResponseEntity.ok(ApiResponse.badRequest(message));
    }

    /**
     * 处理找不到处理器异常（404错误）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.error("No handler found: {}", ex.getMessage());
        String message = String.format("No handler found for %s request '%s'", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.ok(ApiResponse.notFound(message));
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.error("Method not supported: {}", ex.getMessage());
        String message = String.format("Method not supported: '%s', supported methods are: %s", ex.getMethod(), ex.getSupportedHttpMethods());
        return ResponseEntity.ok(ApiResponse.methodNotAllowed(message));
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        logger.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.unauthorized("Authentication failed: " + ex.getMessage()));
    }

    /**
     * 处理认证信息不足异常
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        logger.error("Insufficient authentication: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.unauthorized("No authentication information provided or insufficient"));
    }

    /**
     * 处理数据完整性冲突异常（数据库约束冲突）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.error("Data integrity conflict, possibly due to unique key conflict", HttpStatus.CONFLICT.value()));
    }

    /**
     * 处理所有未捕获的全局异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Global exception: ", ex);
        return ResponseEntity.ok(ApiResponse.serverError("Server internal error: " + ex.getMessage()));
    }
}