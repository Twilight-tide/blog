package com.twilight.blog.interceptor;

import com.twilight.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Header 中获取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token无效\"}");
            return false;
        }

        // 去掉 "Bearer " 前缀
        token = token.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null || jwtUtil.isTokenExpired(token)) {
                response.setStatus(401);
                response.getWriter().write("{\"code\":401,\"msg\":\"Token已过期\"}");
                return false;
            }
            // 将用户名存入请求属性，供后续使用
            request.setAttribute("username", username);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效\"}");
            return false;
        }
    }
}