package com.twilight.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @NotNull(message = "文章 ID 不能为空")
    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private Long userId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论不能超过 1000 个字符")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long parentId = 0L;  // 0 表示顶级评论
}