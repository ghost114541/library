package com.library.admin.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.library.admin.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 实现Spring Security UserDetails接口的用户详情类
 * 封装用户身份信息、权限和认证状态
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L; // 序列化版本号

    // 用户唯一标识
    private final Long userId;

    // 用户名（登录凭证）
    private final String username;

    // 密码字段使用JsonIgnore注解防止序列化泄露敏感信息
    @JsonIgnore
    private final String password;

    // 用户邮箱
    private final String email;

    // 用户权限集合（Spring Security认证所需）
    private final Collection<? extends GrantedAuthority> authorities;

    // 用户角色类型（来自实体类的枚举类型）
    private final User.UserRole role;

    /**
     * 构造函数
     * @param userId 用户ID
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param authorities 权限集合
     * @param role 用户角色
     */
    public UserDetailsImpl(Long userId, String username, String password, String email,
                           Collection<? extends GrantedAuthority> authorities, User.UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.role = role;
    }

    /**
     * 工厂方法：从User实体对象构建UserDetailsImpl实例
     * @param user 用户实体对象
     * @return UserDetailsImpl实例
     */
    public static UserDetailsImpl build(User user) {
        // 将用户角色转换为Spring Security的GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        return new UserDetailsImpl(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                Collections.singletonList(authority),
                user.getRole()
        );
    }

    /**
     * 获取用户权限集合（Spring Security认证接口要求）
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 获取用户邮箱
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取用户角色
     * @return 用户角色枚举
     */
    public User.UserRole getRole() {
        return role;
    }

    /**
     * 获取加密密码（Spring Security认证接口要求）
     * @return 加密后的密码
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 获取用户名（Spring Security认证接口要求）
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期（默认返回true，实际业务中可能需要根据业务逻辑实现）
     * @return true 表示账户有效
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定（默认返回true，实际业务中可能需要根据业务逻辑实现）
     * @return true 表示账户未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期（默认返回true，实际业务中可能需要根据业务逻辑实现）
     * @return true 表示凭证有效
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用（默认返回true，实际业务中可能需要根据业务逻辑实现）
     * @return true 表示账户已启用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 对象相等性判断（基于用户ID比较）
     * @param o 被比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(userId, user.userId);
    }

    /**
     * 计算哈希码（基于用户ID）
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}