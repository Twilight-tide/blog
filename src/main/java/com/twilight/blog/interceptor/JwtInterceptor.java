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
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token无效\"}");
            return false;
        }

        token = token.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            System.out.println("=== [JwtInterceptor] 解析到用户名: " + username);

            if (username == null || jwtUtil.isTokenExpired(token)) {
                response.setStatus(401);
                response.getWriter().write("{\"code\":401,\"msg\":\"Token已过期\"}");
                return false;
            }

            // ★ 关键：把用户名存入 request，供 Controller 使用
            request.setAttribute("username", username);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效\"}");
            return false;
        }
    }
}