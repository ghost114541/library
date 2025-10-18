package com.library.admin.security;

import com.library.admin.entity.User;
import com.library.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口
 * 用于根据用户名从数据库加载用户信息并构建认证所需对象
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 用户数据访问对象
     * 用于操作数据库中的用户表
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详细信息
     * Spring Security认证流程中会调用此方法
     *
     * @param username 登录时提供的用户名
     * @return UserDetails实例（包含用户权限等认证所需信息）
     * @throws UsernameNotFoundException 如果未找到对应用户
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户实体对象
        // 如果未找到用户则抛出异常中断认证流程
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 将用户实体转换为Spring Security的UserDetails实现类
        // 该实现类包含认证所需的所有信息（用户名、密码、权限等）
        return UserDetailsImpl.build(user);
    }
}