package com.library.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.admin.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证入口点处理类
 * 用于处理未认证用户访问受保护资源时的异常情况
 */
@Component // 声明为Spring组件
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    // JSON序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 认证失败时的处理方法
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param authException 认证异常对象
     * @throws IOException 输入输出异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 记录未授权错误日志
        logger.error("Unauthorized error: {}", authException.getMessage());

        // 设置响应状态码为401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容类型为JSON格式
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 创建统一的API错误响应对象
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "认证失败: " + authException.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );

        // 将错误响应对象序列化为JSON并写入响应输出流
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}