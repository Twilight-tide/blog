package com.twilight.blog.controller;

import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Article;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @PostMapping("/publish")
    public R<Article> publish(@Valid @RequestBody Article article, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return R.error("用户不存在");
        }

        article.setAuthorId(user.get().getId());
        article.setPublished(true);
        article.setViewCount(0);
        article.setLikeCount(0);

        Article saved = articleRepository.save(article);
        return R.ok(saved);
    }

    // ====== 新增接口1：公开文章列表 ======
    @GetMapping("/list")
    public R<List<Article>> list() {
        // 只返回已发布的文章，按时间倒序
        List<Article> articles = articleRepository.findByPublishedTrueOrderByCreateTimeDesc();
        return R.ok(articles);
    }

    // ====== 新增接口2：文章详情（公开） ======
    @GetMapping("/{id}")
    public R<Article> detail(@PathVariable Long id) {
        // 只查询已发布的文章
        var article = articleRepository.findByIdAndPublishedTrue(id);
        if (article.isEmpty()) {
            return R.error("文章不存在或未发布");
        }

        // 简单增加浏览量（每次访问+1，生产环境需要用Redis防刷）
        Article a = article.get();
        a.setViewCount(a.getViewCount() + 1);
        articleRepository.save(a);

        return R.ok(a);
    }
}