package com.twilight.blog.service.impl;

import com.twilight.blog.entity.User;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // 密码加密器（Spring Security 提供）
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(User user) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 密码加密（绝对不能存明文！）
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 保存到数据库
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}