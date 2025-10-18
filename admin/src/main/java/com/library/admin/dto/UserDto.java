package com.library.admin.dto;

import com.library.admin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 用户数据传输对象（DTO）
 * 用于封装用户相关API请求和响应数据
 * 包含用户基本信息及多种操作的请求参数类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    /**
     * 用户ID
     * 唯一标识符（由系统自动生成）
     */
    private Long userId;

    /**
     * 用户名
     * 必填字段（不能为空字符串）
     * 用于登录和显示的唯一标识
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * 电子邮箱
     * 格式要求：符合标准邮箱格式
     * 示例：user@example.com
     */
    @Email(message = "Email should be valid")
    private String email;

    /**
     * 电话号码
     * 格式要求：10到15位纯数字
     * 示例：13800000000
     */
    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    /**
     * 用户角色
     * 枚举类型（User.UserRole）
     * 可能取值：
     * - ADMIN（管理员）
     * - USER（普通用户）
     */
    private User.UserRole role;

    /**
     * 创建时间
     * 格式：yyyy-MM-dd HH:mm:ss
     * 记录用户账号创建的具体时间
     */
    private LocalDateTime createTime;

    /**
     * 用户注册请求参数类
     * 用于封装POST /register请求的数据
     * 包含必填字段和验证规则
     */
    public static class RegisterRequest {
        /**
         * 用户名
         * 必填字段（不能为空字符串）
         * 用于登录的唯一标识
         */
        @NotBlank(message = "Username is required")
        private String username;

        /**
         * 密码
         * 必填字段，要求：
         * - 至少8个字符
         * - 同时包含字母和数字
         * - 不允许特殊字符
         */
        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Password must be at least 8 characters and contain both letters and numbers")
        private String password;

        /**
         * 电子邮箱
         * 格式要求：符合标准邮箱格式
         * 示例：user@example.com
         */
        @Email(message = "Email should be valid")
        private String email;

        /**
         * 电话号码
         * 格式要求：10到15位纯数字
         * 示例：13800000000
         */
        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        private String phone;

        // Getter和Setter方法
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    /**
     * 用户登录请求参数类
     * 用于封装POST /login请求的数据
     * 包含必填字段
     */
    public static class LoginRequest {
        /**
         * 用户名
         * 必填字段（不能为空字符串）
         * 用于登录的唯一标识
         */
        @NotBlank(message = "Username is required")
        private String username;

        /**
         * 密码
         * 必填字段（不能为空字符串）
         * 与注册时设置的密码匹配
         */
        @NotBlank(message = "Password is required")
        private String password;

        // Getter和Setter方法
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 修改密码请求参数类
     * 用于封装PUT /change-password请求的数据
     * 包含密码验证规则
     */
    public static class PasswordChangeRequest {
        /**
         * 当前密码
         * 必填字段（不能为空字符串）
         * 需与数据库中存储的密码匹配
         */
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        /**
         * 新密码
         * 必填字段，要求：
         * - 至少8个字符
         * - 同时包含字母和数字
         * - 不允许特殊字符
         */
        @NotBlank(message = "New password is required")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Password must be at least 8 characters and contain both letters and numbers")
        private String newPassword;

        // Getter和Setter方法
        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * JWT认证响应数据类
     * 用于返回登录成功后的认证令牌信息
     */
    public static class JwtResponse {
        /**
         * JWT访问令牌
         * 用于后续API请求的身份验证
         * 示例：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
         */
        private String token;

        /**
         * 令牌类型
         * 默认值：Bearer
         * 用于请求头中的Authorization字段
         */
        private String type = "Bearer";

        /**
         * 用户ID
         * 关联到当前认证用户的唯一标识
         */
        private Long userId;

        /**
         * 用户名
         * 当前认证用户的显示名称
         */
        private String username;

        /**
         * 用户角色
         * 枚举类型（User.UserRole）
         * 可能取值：
         * - ADMIN（管理员）
         * - USER（普通用户）
         */
        private User.UserRole role;

        // 构造方法
        public JwtResponse(String token, Long userId, String username, User.UserRole role) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        // Getter和Setter方法
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public User.UserRole getRole() {
            return role;
        }

        public void setRole(User.UserRole role) {
            this.role = role;
        }
    }
}