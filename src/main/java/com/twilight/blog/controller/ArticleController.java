package com.twilight.blog.controller;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Article;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.utils.HtmlSanitizer;
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

    // ====== 发布文章（需认证） ======
    @PostMapping("/publish")
    public R<Article> publish(@Valid @RequestBody Article article, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return R.error("用户不存在，请重新登录");
        }

        // 设置文章属性
        article.setAuthorId(user.get().getId());
        article.setPublished(true);
        article.setViewCount(0);
        article.setLikeCount(0);

        // 入库前清洗富文本
        article.setContent(HtmlSanitizer.clean(article.getContent()));
        return R.ok(articleRepository.save(article));
    }

    // ====== 公开文章列表 ======
    @PublicApi
    @GetMapping("/list")
    public R<List<Article>> list() {
        List<Article> articles = articleRepository.findByPublishedTrueOrderByCreateTimeDesc();
        return R.ok(articles);
    }

    // ====== 文章详情（公开） ======
    @PublicApi
    @GetMapping("/{id}")
    public R<Article> detail(@PathVariable Long id) {
        var article = articleRepository.findByIdAndPublishedTrue(id);
        if (article.isEmpty()) {
            return R.error("文章不存在或未发布");
        }

        // 增加浏览量
        Article a = article.get();
        a.setViewCount(a.getViewCount() + 1);
        articleRepository.save(a);

        return R.ok(a);
    }

    // ====== 更新文章（需认证） ======
    @PutMapping("/{id}")
    public R<Article> update(@PathVariable Long id, @Valid @RequestBody Article article, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            return R.error("文章不存在");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getId().equals(existing.get().getAuthorId())) {
            return R.error("没有权限修改此文章");
        }

        Article a = existing.get();
        a.setTitle(article.getTitle());
        a.setContent(HtmlSanitizer.clean(article.getContent()));
        a.setSummary(article.getSummary());
        a.setCategory(article.getCategory());

        Article saved = articleRepository.save(a);
        return R.ok(saved);
    }

    // ====== 删除文章（需认证） ======
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            return R.error("文章不存在");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getId().equals(existing.get().getAuthorId())) {
            return R.error("没有权限删除此文章");
        }

        articleRepository.deleteById(id);
        return R.ok("删除成功");
    }
}