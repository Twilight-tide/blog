package com.twilight.blog.dto.admin;

import com.twilight.blog.entity.Comment;

import java.time.LocalDateTime;

/** 后台评论列表项（比实体多用户名与文章标题）。 */
public record CommentAdminDto(
        Long id,
        Long articleId,
        String articleTitle,
        Long userId,
        String username,
        String content,
        LocalDateTime createTime
) {
    public static CommentAdminDto from(Comment c, String username, String articleTitle) {
        return new CommentAdminDto(
                c.getId(), c.getArticleId(), articleTitle,
                c.getUserId(), username, c.getContent(), c.getCreateTime()
        );
    }
}
