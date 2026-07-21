package com.twilight.blog.controller;

import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Article;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @PostMapping("/publish")
    public R<Article> publish(@Valid @RequestBody Article article, HttpServletRequest request) {
        // 从拦截器存入的 username 获取当前用户
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
}