package com.twilight.blog.repository;

import com.twilight.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorIdOrderByCreateTimeDesc(Long authorId);

    // 1. 查询所有已发布的文章，按创建时间倒序（最新在前）
    List<Article> findByPublishedTrueOrderByCreateTimeDesc();

    // 1b. 分页版本（page/size 由调用方决定）
    Page<Article> findByPublishedTrueOrderByCreateTimeDesc(Pageable pageable);

    // 2. 查询单篇已发布的文章（确保只能看到已发布的）
    java.util.Optional<Article> findByIdAndPublishedTrue(Long id);

    // 3. 原子自增浏览量，避免并发下读-改-写丢更新
    @Modifying
    @Transactional
    @Query("update Article a set a.viewCount = a.viewCount + 1 where a.id = :id and a.published = true")
    int incrementViewCount(@Param("id") Long id);
}