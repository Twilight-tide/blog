package com.twilight.blog.common.util;

import java.security.SecureRandom;

/**
 * 生成管理员重置密码时使用的随机临时密码。
 */
public final class PasswordGenerator {

    // 去掉了容易混淆的 0/O/1/l/I
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String random(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String random() {
        return random(12);
    }
}
