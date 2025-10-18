package com.library.admin.service.impl;

import com.library.admin.dto.UserDto;
import com.library.admin.entity.User;
import com.library.admin.exception.ResourceAlreadyExistsException;
import com.library.admin.exception.ResourceNotFoundException;
import com.library.admin.repository.UserRepository;
import com.library.admin.security.JwtTokenProvider;
import com.library.admin.security.UserDetailsImpl;
import com.library.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户服务实现类
 * 提供用户注册、登录、资料管理、角色管理等核心功能
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository; // 用户数据访问对象

    @Autowired
    private PasswordEncoder passwordEncoder; // 密码加密工具

    @Autowired
    private AuthenticationManager authenticationManager; // 认证管理器

    @Autowired
    private JwtTokenProvider tokenProvider; // JWT令牌生成器

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求数据（含用户名/密码/邮箱/电话）
     * @return 注册成功的用户信息
     * @throws ResourceAlreadyExistsException 如果用户名/邮箱/电话已存在
     */
    @Override
    @Transactional
    public UserDto registerUser(UserDto.RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", registerRequest.getUsername());
        }

        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", registerRequest.getEmail());
        }

        // 检查电话是否已存在
        if (registerRequest.getPhone() != null && userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new ResourceAlreadyExistsException("User", "phone", registerRequest.getPhone());
        }

        // 创建新用户并设置默认角色
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRole(User.UserRole.ROLE_USER); // 默认用户角色

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    /**
     * 用户登录认证
     *
     * @param loginRequest 登录请求数据（用户名/密码）
     * @return JWT令牌及用户信息
     * @throws ResourceNotFoundException 如果用户不存在
     * @throws BadCredentialsException 如果密码错误
     */
    @Override
    public UserDto.JwtResponse authenticateUser(UserDto.LoginRequest loginRequest) {
        // 先检查用户是否存在（Spring Security认证前验证）
        if (!userRepository.existsByUsername(loginRequest.getUsername())) {
            throw new ResourceNotFoundException("User not found");
        }

        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            String jwt = tokenProvider.generateToken(authentication);

            // 获取认证后的用户信息
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return new UserDto.JwtResponse(
                    jwt,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getRole()
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password");
        }
    }

    /**
     * 获取当前用户资料
     *
     * @param authentication 认证信息（包含当前用户）
     * @return 当前用户信息
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    public UserDto getUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        return mapToDto(user);
    }

    /**
     * 修改当前用户密码
     *
     * @param authentication 认证信息（包含当前用户）
     * @param request 密码修改请求（旧密码/新密码）
     * @throws IllegalArgumentException 如果旧密码错误
     */
    @Override
    @Transactional
    public void changePassword(Authentication authentication, UserDto.PasswordChangeRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 更新新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 分页查询所有用户
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDto);
    }

    /**
     * 更新用户角色（管理员专用）
     *
     * @param userId 用户ID
     * @param role 新角色
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional
    public UserDto updateUserRole(Long userId, User.UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setRole(role);
        User updatedUser = userRepository.save(user);

        return mapToDto(updatedUser);
    }

    /**
     * 删除用户（管理员专用）
     *
     * @param userId 用户ID
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        userRepository.deleteById(userId);
    }

    /**
     * 将用户实体转换为数据传输对象（DTO）
     *
     * @param user 用户实体
     * @return 对应的DTO对象
     */
    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }
}