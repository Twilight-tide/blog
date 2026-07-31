package com.twilight.blog.repository;

import com.twilight.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询某篇文章的所有顶级评论（按时间倒序）
    List<Comment> findByArticleIdAndParentIdOrderByCreateTimeDesc(Long articleId, Long parentId);

    // 查询某篇文章的所有子评论（按时间正序）
    List<Comment> findByArticleIdAndParentIdOrderByCreateTimeAsc(Long articleId, Long parentId);
}