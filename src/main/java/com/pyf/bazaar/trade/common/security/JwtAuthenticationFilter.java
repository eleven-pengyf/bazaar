package com.pyf.bazaar.trade.common.security;

import com.pyf.bazaar.trade.common.context.UserContext;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. 从 Header 中提取 Token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                // 2. 解析 Token 获取 userId
                String userIdStr = jwtTokenUtil.getUserIdFromToken(token);
                userId = Long.parseLong(userIdStr);
                log.debug("JWT 解析成功，userId: {}", userId);
            } catch (ExpiredJwtException e) {
                log.warn("JWT 已过期: {}", e.getMessage());
                // 过期则不设置上下文，让后续逻辑返回 401
            } catch (SignatureException | MalformedJwtException e) {
                log.warn("JWT 签名无效或格式错误: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("JWT 解析异常: {}", e.getMessage());
            }
        }

        // 3. 如果解析成功，注入上下文
        if (userId != null) {
            // 存入自定义 ThreadLocal
            UserContext.setUserId(userId);

            // 存入 Spring Security 上下文（为了配合 SecurityFilterChain 的 .authenticated() 校验）
            UserDetails userDetails = new User(userId.toString(), "", new ArrayList<>());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            // 4. 继续执行后续过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 5. 请求结束，清除 ThreadLocal，防止内存泄漏（重要！）
            UserContext.clear();
        }
    }
}
