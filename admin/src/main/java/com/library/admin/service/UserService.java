package com.library.admin.service;

import com.library.admin.dto.UserDto;
import com.library.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

/**
 * 用户服务接口
 * 提供用户注册、登录认证、资料管理、角色管理等核心功能
 */
public interface UserService {
    /**
     * 用户注册
     *
     * @param registerRequest 注册请求数据（用户名/密码/邮箱/电话）
     * @return 注册成功的用户信息
     */
    UserDto registerUser(UserDto.RegisterRequest registerRequest);

    /**
     * 用户登录认证
     *
     * @param loginRequest 登录请求数据（用户名/密码）
     * @return JWT令牌及用户信息
     */
    UserDto.JwtResponse authenticateUser(UserDto.LoginRequest loginRequest);

    /**
     * 获取当前用户资料
     *
     * @param authentication 认证信息（包含当前用户）
     * @return 当前用户信息
     */
    UserDto getUserProfile(Authentication authentication);

    /**
     * 修改当前用户密码
     *
     * @param authentication 认证信息（包含当前用户）
     * @param request 密码修改请求（旧密码/新密码）
     * @throws IllegalArgumentException 如果旧密码错误
     */
    void changePassword(Authentication authentication, UserDto.PasswordChangeRequest request);

    /**
     * 分页查询所有用户（管理员专用）
     *
     * @param pageable 分页参数（页码/每页数量）
     * @return 用户分页结果
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * 更新用户角色（管理员专用）
     *
     * @param userId 要更新的用户ID
     * @param role 新角色（如ROLE_ADMIN/ROLE_USER）
     * @return 更新后的用户信息
     */
    UserDto updateUserRole(Long userId, User.UserRole role);

    /**
     * 删除用户（管理员专用）
     *
     * @param userId 要删除的用户ID
     */
    void deleteUser(Long userId);
}