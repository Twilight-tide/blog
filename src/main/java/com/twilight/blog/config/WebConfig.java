package com.twilight.blog.config;

import com.twilight.blog.interceptor.AdminInterceptor;
import com.twilight.blog.interceptor.JwtInterceptor;
import com.twilight.blog.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final UploadService uploadService;

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

    /** 把 /uploads/** 映射到本地上传目录，图片可以直接被浏览器访问。 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadService.getUploadDir().toUri().toString());
    }
}
