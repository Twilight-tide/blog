package com.twilight.blog.interceptor;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.entity.User;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器：校验 JWT 并加载当前用户。
 *
 * <p>每次请求都从数据库读取用户，而不是把角色写进 token。
 * 这样管理员降级或封禁账号后立即生效，旧 token 无法继续使用。</p>
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 贴了 @PublicApi 的方法/类直接放行（公开接口）
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.hasMethodAnnotation(PublicApi.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(PublicApi.class)) {
                return true;
            }
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return reject(response, 401, "未登录或Token无效");
        }

        String username;
        try {
            token = token.substring(7);
            username = jwtUtil.extractUsername(token);
            if (username == null || jwtUtil.isTokenExpired(token)) {
                return reject(response, 401, "Token已过期");
            }
        } catch (Exception e) {
            return reject(response, 401, "Token无效");
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return reject(response, 401, "用户不存在，请重新登录");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            return reject(response, 403, "账号已被禁用，请联系管理员");
        }

        request.setAttribute(AuthAttributes.CURRENT_USER, user);
        return true;
    }

    private boolean reject(HttpServletResponse response, int status, String msg) throws Exception {
        response.setStatus(status);
        response.getWriter().write("{\"code\":" + status + ",\"msg\":\"" + msg + "\"}");
        return false;
    }
}
