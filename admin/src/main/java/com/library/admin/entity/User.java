package com.library.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库表 users，包含用户基本信息和权限信息
 * 使用 JPA 注解进行数据库字段映射
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    /**
     * 用户ID
     * 主键字段，由数据库自动生成（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     * 必填字段，不能为空且具有唯一性
     * 用于登录和显示的唯一标识
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 密码
     * 必填字段，不能为空
     * 存储格式：加密后的密码（如 BCrypt）
     */
    @Column(nullable = false)
    private String password;

    /**
     * 电子邮箱
     * 唯一字段，允许为空
     * 用于账号验证和通知
     */
    @Column(unique = true)
    private String email;

    /**
     * 电话号码
     * 唯一字段，允许为空
     * 格式要求：10-15位纯数字
     */
    @Column(unique = true)
    private String phone;

    /**
     * 用户角色
     * 枚举类型（UserRole）
     * 默认值：ROLE_USER
     * 可能取值：
     * - ROLE_USER（普通用户）
     * - ROLE_ADMIN（管理员）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_USER;

    /**
     * 创建时间
     * 自动填充字段，由数据库在创建记录时生成
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 自动填充字段，由数据库在更新记录时自动更新
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 用户角色枚举
     * 定义用户的权限等级
     */
    public enum UserRole {
        /**
         * ROLE_USER：普通用户
         * 具有基本借阅权限
         */
        ROLE_USER,

        /**
         * ROLE_ADMIN：管理员
         * 具有系统管理权限
         */
        ROLE_ADMIN
    }
}