package com.twilight.blog.repository;

import com.twilight.blog.entity.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询某篇文章的所有顶级评论（按时间倒序）
    List<Comment> findByArticleIdAndParentIdOrderByCreateTimeDesc(Long articleId, Long parentId);

    // 查询某篇文章的所有子评论（按时间正序）
    List<Comment> findByArticleIdAndParentIdOrderByCreateTimeAsc(Long articleId, Long parentId);

    // 后台：按文章筛选评论（文章 ID 为 null 时调用 findAll）
    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    long countByArticleId(Long articleId);

    // 删除文章时级联清理评论，避免留下孤儿数据
    @Modifying
    @Transactional
    @Query("delete from Comment c where c.articleId = :articleId")
    int deleteByArticleId(@Param("articleId") Long articleId);
}
