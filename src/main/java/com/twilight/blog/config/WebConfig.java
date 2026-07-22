package com.twilight.blog.config;

import com.twilight.blog.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/article/**")          // 拦截所有文章接口
                .excludePathPatterns(
                        "/api/test/**",
                        "/api/user/login",
                        "/api/user/register",
                        "/api/article/list",                     // ← 公开列表，不拦截
                        "/api/article/{id}"                      // ← 公开详情，不拦截
                );
    }
}