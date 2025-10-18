package com.library.admin.controller;

import com.library.admin.dto.ApiResponse;
import com.library.admin.dto.UserDto;
import com.library.admin.exception.ResourceNotFoundException;
import com.library.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器类 - 提供用户管理相关接口
 * 所有接口统一路径前缀：/api/users
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户服务组件 - 用于调用用户相关的业务逻辑
     */
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * POST 请求 /api/users/register
     *
     * @param registerRequest 用户注册请求参数（包含验证注解）
     * @return 包含注册结果的ApiResponse响应对象（状态码201 Created）
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody UserDto.RegisterRequest registerRequest) {
        // 调用服务层完成用户注册
        UserDto userDto = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userDto));
    }

    /**
     * 用户登录接口
     * POST 请求 /api/users/login
     *
     * @param loginRequest 用户登录请求参数（包含验证注解）
     * @return 包含JWT令牌的ApiResponse响应对象
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto.JwtResponse>> loginUser(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        try {
            // 调用服务层完成用户认证并返回JWT令牌
            UserDto.JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("登录成功", jwtResponse));
        } catch (ResourceNotFoundException e) {
            // 用户不存在时返回404状态码（但HTTP状态保持200）
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        } catch (BadCredentialsException e) {
            // 密码错误时返回401状态码（但HTTP状态保持200）
            return ResponseEntity.ok(ApiResponse.unauthorized(e.getMessage()));
        } catch (Exception e) {
            // 其他异常返回500状态码（但HTTP状态保持200）
            return ResponseEntity.ok(ApiResponse.serverError("登录失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户个人资料
     * GET 请求 /api/users/profile
     *
     * @param authentication Spring Security认证对象（自动注入）
     * @return 包含用户信息的ApiResponse响应对象
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfile(Authentication authentication) {
        // 通过认证对象获取当前用户信息
        UserDto userDto = userService.getUserProfile(authentication);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    /**
     * 修改用户密码
     * PUT 请求 /api/users/{userId}/password
     *
     * @param authentication Spring Security认证对象（自动注入）
     * @param passwordChangeRequest 密码修改请求参数（包含验证注解）
     * @return 包含操作结果的ApiResponse响应对象
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody UserDto.PasswordChangeRequest passwordChangeRequest) {
        // 调用服务层完成密码修改
        userService.changePassword(authentication, passwordChangeRequest);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}