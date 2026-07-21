package com.twilight.blog.service;

import com.twilight.blog.entity.User;

public interface UserService {

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册后的用户（含id）
     * @throws RuntimeException 如果用户名已存在
     */
    User register(User user);
    User findByUsername(String username);
}