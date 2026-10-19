package com.twilight.blog.common.constant;

/**
 * 认证信息在请求属性中的键名，避免字符串散落在各处。
 */
public final class AuthAttributes {

    private AuthAttributes() {
    }

    /** JwtInterceptor 校验通过后写入的当前登录用户（User 实体）。 */
    public static final String CURRENT_USER = "currentUser";
}
