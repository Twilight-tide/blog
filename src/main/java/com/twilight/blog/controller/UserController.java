package com.twilight.blog.controller;

import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.User;
import com.twilight.blog.service.UserService;
import com.twilight.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public R<User> register(@Valid @RequestBody User user) {
        return R.ok(userService.register(user));
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 用户名与密码错误使用同一提示，避免暴露账号是否存在
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException(403, "该账号已被禁用，请联系管理员");
        }

        userService.recordLogin(user.getId());

        String token = jwtUtil.generateToken(user.getUsername());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole().name());
        return R.ok(data);
    }

    /** 获取当前登录用户的最新信息（前端刷新页面后同步角色变化）。 */
    @GetMapping("/me")
    public R<User> me(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthAttributes.CURRENT_USER);
        return R.ok(user);
    }
}
