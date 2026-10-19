package com.twilight.blog.dto.admin;

import com.twilight.blog.entity.Article;

import java.time.LocalDateTime;

/** 后台文章列表项（比实体多一个作者名）。 */
public record ArticleAdminDto(
        Long id,
        String title,
        String summary,
        String category,
        Long authorId,
        String authorName,
        Boolean published,
        Integer viewCount,
        Integer likeCount,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
    public static ArticleAdminDto from(Article a, String authorName) {
        return new ArticleAdminDto(
                a.getId(), a.getTitle(), a.getSummary(), a.getCategory(),
                a.getAuthorId(), authorName, a.getPublished(),
                a.getViewCount(), a.getLikeCount(), a.getCreateTime(), a.getUpdateTime()
        );
    }
}
