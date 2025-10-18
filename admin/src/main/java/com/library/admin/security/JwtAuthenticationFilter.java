package com.library.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * JWT身份验证过滤器，用于在请求处理前验证JWT令牌
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // 定义无需JWT验证的路径白名单
    private static final List<String> AUTH_WHITELIST = Arrays.asList(
            "/api/users/register",  // 用户注册接口
            "/api/users/login",     // 用户登录接口
            "/error"                // 全局错误处理接口
    );

    // 路径匹配工具，用于判断请求路径是否匹配白名单模式
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JwtTokenProvider tokenProvider;  // JWT工具类，用于令牌生成和解析

    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // 用户详情服务

    /**
     * 判断当前请求是否需要被过滤
     * @param request HTTP请求对象
     * @return 如果返回true则跳过过滤，返回false则执行过滤逻辑
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 允许白名单中的路径无需验证
        boolean inWhitelist = AUTH_WHITELIST.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        // 允许对/api/books及其子路径的GET请求无需验证
        boolean isGetBooksRequest = HttpMethod.GET.matches(method) &&
                (pathMatcher.match("/api/books", path) || pathMatcher.match("/api/books/*", path));

        return inWhitelist || isGetBooksRequest;
    }

    /**
     * 核心过滤逻辑，处理JWT身份验证
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头中提取JWT令牌
            String jwt = getJwtFromRequest(request);

            // 如果令牌存在且有效，则进行认证
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 从令牌中获取用户名
                String username = tokenProvider.getUsernameFromToken(jwt);

                // 加载用户详细信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息存入SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            // 处理过期令牌
            logger.warn("Expired JWT token: {}", ex.getMessage());
            request.setAttribute("expired", ex.getMessage());
        } catch (Exception ex) {
            // 处理其他验证异常
            logger.error("Could not set user authentication in security context", ex);
            request.setAttribute("invalid_token", ex.getMessage());
        }

        // 继续执行后续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析JWT令牌
     * @param request HTTP请求对象
     * @return 提取的JWT字符串或null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 从Authorization头获取Bearer令牌
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // 去除"Bearer "前缀
        }
        return null;
    }
}