package com.twilight.blog.common.util;

/**
 * 字符串小工具。
 */
public final class Texts {

    private Texts() {
    }

    /** 空白字符串统一转成 null，便于数据库查询判断「不筛选」。 */
    public static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
