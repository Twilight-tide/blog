package com.twilight.blog.config;

import com.twilight.blog.interceptor.AdminInterceptor;
import com.twilight.blog.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 先认证：解析 token、加载当前用户
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register"
                )
                .order(1);

        // 2. 再鉴权：后台接口只允许管理员
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(2);
    }
}
