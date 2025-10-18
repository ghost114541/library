package com.library.admin.security;

import com.library.admin.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // 从配置文件注入JWT密钥
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 从配置文件注入JWT过期时间（毫秒）
    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    // 存储HMAC-SHA签名密钥
    private Key key;

    // Spring Bean初始化方法
    @PostConstruct
    public void init() {
        // 使用Base64编码确保密钥长度符合要求
        String encodedKey = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        // 通过JWT库生成符合规范的签名密钥
        this.key = Keys.hmacShaKeyFor(encodedKey.getBytes());

        logger.info("JWT key initialization completed");
    }

    /**
     * 根据认证信息生成JWT令牌
     * @param authentication Spring Security认证对象
     * @return 生成的JWT字符串
     */
    public String generateToken(Authentication authentication) {
        // 获取用户详细信息
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 当前时间与过期时间计算
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                // 设置令牌主题（用户名）
                .setSubject(userDetails.getUsername())
                // 设置签发时间
                .setIssuedAt(now)
                // 设置过期时间
                .setExpiration(expiryDate)
                // 添加自定义声明：用户ID
                .claim("userId", userDetails.getUserId())
                // 添加自定义声明：用户角色
                .claim("role", userDetails.getRole())
                // 使用密钥和HS512算法签名
                .signWith(key, SignatureAlgorithm.HS512)
                // 生成紧凑格式的JWT字符串
                .compact();
    }

    /**
     * 从JWT令牌中提取用户名
     * @param token JWT字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            // 解析JWT令牌并获取声明体
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 返回令牌主题（用户名）
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Cannot get username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证JWT令牌有效性
     * @param token JWT字符串
     * @return 令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 尝试解析并验证令牌
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
} 