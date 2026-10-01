package com.twilight.blog.controller;

import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.User;
import com.twilight.blog.service.UserService;
import com.twilight.blog.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
        try {
            User registered = userService.register(user);
            return R.ok(registered);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    // ========== 新增登录接口 ==========
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. 根据用户名查询用户
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                return R.error("用户名或密码错误");
            }

            // 2. 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return R.error("用户名或密码错误");
            }

            // 3. 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUsername());

            // 4. 返回 Token
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            return R.ok(data);
        } catch (Exception e) {
            return R.error("登录失败：" + e.getMessage());
        }
    }
}