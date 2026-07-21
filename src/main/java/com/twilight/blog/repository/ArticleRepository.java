package com.twilight.blog.repository;

import com.twilight.blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorIdOrderByCreateTimeDesc(Long authorId);
    List<Article> findByPublishedTrueOrderByCreateTimeDesc();
}