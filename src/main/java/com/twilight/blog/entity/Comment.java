package com.twilight.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long parentId = 0L;  // 0 表示顶级评论
}