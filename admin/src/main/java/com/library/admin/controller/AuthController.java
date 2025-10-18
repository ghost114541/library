package com.library.admin.controller;

import com.library.admin.dto.ApiResponse;
import com.library.admin.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器类 - 提供令牌验证相关接口
 * 接口统一路径前缀：/api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 令牌有效性验证接口
     * GET 请求 /api/auth/check
     *
     * @param authentication Spring Security认证对象（由框架自动注入）
     * @return 包含令牌状态和用户信息的ApiResponse响应对象
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkToken(Authentication authentication) {
        // 判断认证对象是否存在且已通过验证
        if (authentication != null && authentication.isAuthenticated()) {
            // 获取用户详情信息（需要强制类型转换为自定义的UserDetailsImpl）
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // 构建包含用户详细信息的返回数据
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("valid", true);            // 标记令牌有效
            tokenInfo.put("username", userDetails.getUsername());  // 用户名
            tokenInfo.put("userId", userDetails.getUserId());      // 用户ID
            tokenInfo.put("role", userDetails.getRole());          // 用户角色
            tokenInfo.put("authorities", userDetails.getAuthorities());  // 权限列表

            // 返回包含成功状态和用户信息的API响应
            return ResponseEntity.ok(ApiResponse.success("Token有效", tokenInfo));
        }

        // 理论上不会执行到此分支（无效认证会被Spring Security拦截）
        // 保留此逻辑作为兜底方案
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("valid", false);  // 标记令牌无效

        // 返回包含失败状态的API响应
        return ResponseEntity.ok(ApiResponse.success("Token无效", tokenInfo));
    }
}