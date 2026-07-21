package com.twilight.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（前后端分离必须）
                .csrf(csrf -> csrf.disable())

                // 关键改动：所有请求都放行，让拦截器来管理认证
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // ← 全部放行，不再走 Security 的认证
                );

        return http.build();
    }
}