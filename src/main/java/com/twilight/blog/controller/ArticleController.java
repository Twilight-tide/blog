package com.twilight.blog.controller;

import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Article;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // ====== 发布文章（需认证） ======
    @PostMapping("/publish")
    public R<Article> publish(@Valid @RequestBody Article article, HttpServletRequest request) {
        System.out.println("========================================");
        System.out.println("=== [publish] 方法被调用 ===");
        System.out.println("========================================");

        // 打印所有请求头
        System.out.println("=== [publish] 所有请求头 ===");
        Collections.list(request.getHeaderNames())
                .forEach(name -> System.out.println("  " + name + ": " + request.getHeader(name)));

        // 方式一：从拦截器获取 username
        String username = (String) request.getAttribute("username");
        System.out.println("=== [publish] 从拦截器获取的 username: " + username);

        // 方式二：如果拦截器没传，直接从 Token 解析（兜底方案）
        if (username == null || username.isEmpty()) {
            System.out.println("=== [publish] 拦截器未设置 username，尝试从 Token 解析 ===");
            String token = request.getHeader("Authorization");
            System.out.println("=== [publish] Authorization 头: " + token);

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("=== [publish] 去掉 Bearer 后的 Token 前20位: " + token.substring(0, Math.min(token.length(), 20)) + "...");
                try {
                    username = jwtUtil.extractUsername(token);
                    System.out.println("=== [publish] 从 Token 解析的 username: " + username);
                } catch (Exception e) {
                    System.out.println("=== [publish] Token 解析失败: " + e.getMessage());
                    e.printStackTrace();
                    return R.error("Token 无效: " + e.getMessage());
                }
            } else {
                System.out.println("=== [publish] 没有 Authorization 头或格式不正确");
                return R.error("请先登录");
            }
        }

        // 查询用户
        System.out.println("=== [publish] 准备查询用户: " + username);
        var user = userRepository.findByUsername(username);
        System.out.println("=== [publish] 查询到的用户: " + user);

        if (user.isEmpty()) {
            System.out.println("=== [publish] 用户不存在！");
            return R.error("用户不存在，请重新登录");
        }

        System.out.println("=== [publish] 用户ID: " + user.get().getId());

        // 设置文章属性
        article.setAuthorId(user.get().getId());
        article.setPublished(true);
        article.setViewCount(0);
        article.setLikeCount(0);

        System.out.println("=== [publish] 准备保存文章: " + article.getTitle());
        Article saved = articleRepository.save(article);
        System.out.println("=== [publish] 文章保存成功，ID: " + saved.getId());
        System.out.println("========================================");

        return R.ok(saved);
    }

    // ====== 公开文章列表 ======
    @GetMapping("/list")
    public R<List<Article>> list() {
        System.out.println("=== [list] 查询所有已发布文章 ===");
        List<Article> articles = articleRepository.findByPublishedTrueOrderByCreateTimeDesc();
        System.out.println("=== [list] 查询到 " + articles.size() + " 篇文章");
        return R.ok(articles);
    }

    // ====== 文章详情（公开） ======
    @GetMapping("/{id}")
    public R<Article> detail(@PathVariable Long id) {
        System.out.println("=== [detail] 查询文章 ID: " + id);
        var article = articleRepository.findByIdAndPublishedTrue(id);
        if (article.isEmpty()) {
            System.out.println("=== [detail] 文章不存在或未发布");
            return R.error("文章不存在或未发布");
        }

        // 增加浏览量
        Article a = article.get();
        a.setViewCount(a.getViewCount() + 1);
        articleRepository.save(a);
        System.out.println("=== [detail] 文章浏览量+1，当前: " + a.getViewCount());

        return R.ok(a);
    }

    // ====== 更新文章（需认证） ======
    @PutMapping("/{id}")
    public R<Article> update(@PathVariable Long id, @Valid @RequestBody Article article, HttpServletRequest request) {
        System.out.println("=== [update] 更新文章 ID: " + id);

        // 获取当前用户
        String username = (String) request.getAttribute("username");
        System.out.println("=== [update] 从拦截器获取的 username: " + username);

        if (username == null || username.isEmpty()) {
            // 尝试从 Token 解析
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    username = jwtUtil.extractUsername(token);
                    System.out.println("=== [update] 从 Token 解析的 username: " + username);
                } catch (Exception e) {
                    System.out.println("=== [update] Token 解析失败: " + e.getMessage());
                    return R.error("Token 无效");
                }
            }
        }

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            return R.error("文章不存在");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getId().equals(existing.get().getAuthorId())) {
            System.out.println("=== [update] 权限不足: 用户 " + username + " 不是作者");
            return R.error("没有权限修改此文章");
        }

        Article a = existing.get();
        a.setTitle(article.getTitle());
        a.setContent(article.getContent());
        a.setSummary(article.getSummary());
        a.setCategory(article.getCategory());

        Article saved = articleRepository.save(a);
        System.out.println("=== [update] 文章更新成功");
        return R.ok(saved);
    }

    // ====== 删除文章（需认证） ======
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id, HttpServletRequest request) {
        System.out.println("=== [delete] 删除文章 ID: " + id);

        // 获取当前用户
        String username = (String) request.getAttribute("username");
        System.out.println("=== [delete] 从拦截器获取的 username: " + username);

        if (username == null || username.isEmpty()) {
            // 尝试从 Token 解析
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    username = jwtUtil.extractUsername(token);
                    System.out.println("=== [delete] 从 Token 解析的 username: " + username);
                } catch (Exception e) {
                    System.out.println("=== [delete] Token 解析失败: " + e.getMessage());
                    return R.error("Token 无效");
                }
            }
        }

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            return R.error("文章不存在");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getId().equals(existing.get().getAuthorId())) {
            System.out.println("=== [delete] 权限不足: 用户 " + username + " 不是作者");
            return R.error("没有权限删除此文章");
        }

        articleRepository.deleteById(id);
        System.out.println("=== [delete] 文章删除成功");
        return R.ok("删除成功");
    }
}