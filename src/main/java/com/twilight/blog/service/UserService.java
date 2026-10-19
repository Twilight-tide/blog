package com.twilight.blog.service;

import com.twilight.blog.entity.User;

public interface UserService {

    /**
     * 用户注册。新用户固定为 USER 角色、启用状态，忽略请求中传入的角色等敏感字段。
     *
     * @param user 用户信息
     * @return 注册后的用户（含 id）
     * @throws com.twilight.blog.common.exception.BusinessException 如果用户名已存在
     */
    User register(User user);

    User findByUsername(String username);

    User findById(Long id);

    /** 记录登录时间（后台账号列表展示用）。 */
    void recordLogin(Long id);
}
