package com.library.admin.repository;

import com.library.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 * 继承自JpaRepository，提供基本的CRUD操作及自定义查询方法
 */
@Repository // 标识为Spring数据访问层组件
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 包含用户对象的Optional，可能为空
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱地址
     * @return 包含用户对象的Optional，可能为空
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查询用户
     * @param phone 手机号码
     * @return 包含用户对象的Optional，可能为空
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     * @param username 待检查的用户名
     * @return 存在返回true，否则false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 待检查的邮箱地址
     * @return 存在返回true，否则false
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     * @param phone 待检查的手机号码
     * @return 存在返回true，否则false
     */
    boolean existsByPhone(String phone);
}