package com.library.admin.controller;

import com.library.admin.dto.ApiResponse;
import com.library.admin.dto.UserDto;
import com.library.admin.entity.User;
import com.library.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器类 - 提供用户管理相关接口
 * 所有接口需管理员权限（ROLE_ADMIN）访问
 * 接口统一路径前缀：/api/admin
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    /**
     * 用户服务组件 - 用于调用用户相关的业务逻辑
     */
    @Autowired
    private UserService userService;

    /**
     * 获取所有用户分页列表
     * GET 请求 /api/admin/users
     *
     * @param pageable 分页参数（默认每页10条，按用户ID排序）
     * @return 包含分页用户数据的ApiResponse响应对象
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(
            @PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 更新用户角色
     * PUT 请求 /api/admin/users/{userId}
     *
     * @param userId 要更新的用户ID
     * @param role 新用户角色（来自User.UserRole枚举）
     * @return 包含更新后用户数据的ApiResponse响应对象
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam User.UserRole role) {
        UserDto userDto = userService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", userDto));
    }

    /**
     * 删除指定用户
     * DELETE 请求 /api/admin/users/{userId}
     *
     * @param userId 要删除的用户ID
     * @return 包含操作结果的ApiResponse响应对象
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}