package com.twilight.blog.interceptor;

import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.entity.User;
import com.twilight.blog.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台鉴权拦截器：/api/admin/** 仅管理员可访问。
 *
 * <p>必须排在 JwtInterceptor 之后执行（依赖它写入的 currentUser）。</p>
 */
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        User user = (User) request.getAttribute(AuthAttributes.CURRENT_USER);
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token无效\"}");
            return false;
        }
        if (!permissionService.canUseAdmin(user)) {
            response.setStatus(403);
            response.getWriter().write("{\"code\":403,\"msg\":\"需要管理员权限\"}");
            return false;
        }
        return true;
    }
}
