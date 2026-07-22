package com.pyf.bazaar.trade.common.config;

import com.pyf.bazaar.trade.common.handler.AccessDeniedHandlerImpl;
import com.pyf.bazaar.trade.common.handler.AuthenticationEntryPointImpl;
import com.pyf.bazaar.trade.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（无状态 JWT）
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 禁用 Session（完全无状态）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置认证失败和权限不足处理器
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 3. 请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行：认证接口
                        .requestMatchers(HttpMethod.POST, "/user/register", "/user/login").permitAll()
                        // 放行：静态资源
                        .requestMatchers("/", "/index.html", "/app.js", "/favicon.ico").permitAll()
                        // 放行：Knife4j / Swagger 接口文档（放行所有相关资源）
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // 放行：健康检查端点（如果引入了 actuator，才需要放行）
                        .requestMatchers("/actuator/health").permitAll()
                        // 其他所有请求必须认证（即必须携带有效 JWT）
                        .anyRequest().authenticated()
                )
                // 4. 将自定义 JWT 过滤器插入到默认用户名密码过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}