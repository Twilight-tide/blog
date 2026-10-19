package com.twilight.blog.service;

import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后台内容操作（需要跨表事务的业务）。
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    /** 删除文章并级联清理其评论，避免留下孤儿数据。 */
    @Transactional
    public void deleteArticleWithComments(Long articleId) {
        commentRepository.deleteByArticleId(articleId);
        articleRepository.deleteById(articleId);
    }
}
