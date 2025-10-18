package com.library.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * 配置基于JWT的无状态认证和访问控制规则
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,                 // 启用@Secured注解
        jsr250Enabled = true,                 // 启用@RolesAllowed注解
        prePostEnabled = true)                // 启用preAuthorize/postAuthorize注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // 用户详情服务

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;  // 未授权处理入口

    /**
     * 注册JWT身份验证过滤器Bean
     * 用于在认证流程中解析和验证JWT令牌
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * 配置认证管理器
     * 设置用户详情服务和密码编码器
     */
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)  // 使用自定义用户详情服务
                .passwordEncoder(passwordEncoder());     // 使用BCrypt密码编码器
    }

    /**
     * 配置Web安全策略（完全绕过Spring Security过滤链）
     * 用于放行不需要任何安全处理的请求
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login")  // 放行注册和登录接口
                .antMatchers("/error");  // 放行全局错误处理页面
    }

    /**
     * 暴露认证管理器Bean
     * 供其他组件（如认证控制器）使用
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置BCrypt密码编码器
     * 用于安全地存储和验证用户密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 核心安全配置方法
     * 定义HTTP请求的认证和授权规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()  // 禁用CORS和CSRF保护（API通常不需要）
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()  // 配置未授权处理
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()  // 无状态会话
                .authorizeRequests()  // 开始配置请求授权规则
                // 放行无需认证的路径
                .antMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()  // 注册和登录接口
                .antMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()  // 所有图书GET请求
                .antMatchers("/error").permitAll()  // 全局错误处理页面
                // 受限路径的访问控制
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")  // 管理员接口需要ADMIN角色
                .anyRequest().authenticated();  // 所有其他请求必须认证

        // 添加JWT身份验证过滤器
        // 放置在UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}