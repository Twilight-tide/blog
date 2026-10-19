package com.twilight.blog.service.impl;

import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.entity.User;
import com.twilight.blog.entity.UserRole;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // 密码加密器（Spring Security 提供）
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 忽略客户端传入的敏感字段：注册一律是普通用户，防止自行提权
        user.setId(null);
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setRemark(null);
        user.setLastLoginTime(null);

        // 3. 密码加密（绝对不能存明文！）
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 保存到数据库
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void recordLogin(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}
