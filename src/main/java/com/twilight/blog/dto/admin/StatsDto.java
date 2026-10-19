package com.twilight.blog.dto.admin;

/** 后台首页统计数据。 */
public record StatsDto(
        long users,
        long admins,
        long authors,
        long disabledUsers,
        long articles,
        long publishedArticles,
        long comments
) {
}
