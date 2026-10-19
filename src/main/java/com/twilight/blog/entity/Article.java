package com.twilight.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "article")
public class Article extends BaseEntity {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过 200 个字符")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "正文不能为空")
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Size(max = 500, message = "摘要不能超过 500 个字符")
    @Column(length = 500)
    private String summary;

    @Size(max = 100, message = "分类不能超过 100 个字符")
    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Boolean published = false;
}