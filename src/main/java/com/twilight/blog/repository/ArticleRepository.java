package com.twilight.blog.repository;

import com.twilight.blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorIdOrderByCreateTimeDesc(Long authorId);

    // 1. 查询所有已发布的文章，按创建时间倒序（最新在前）
    List<Article> findByPublishedTrueOrderByCreateTimeDesc();

    // 2. 查询单篇已发布的文章（确保只能看到已发布的）
    java.util.Optional<Article> findByIdAndPublishedTrue(Long id);
}