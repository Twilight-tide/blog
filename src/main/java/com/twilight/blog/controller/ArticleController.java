package com.twilight.blog.controller;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.exception.ForbiddenException;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Article;
import com.twilight.blog.entity.User;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.service.PermissionService;
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
    private final PermissionService permissionService;

    // ====== 发布文章（需 ADMIN 或 AUTHOR） ======
    @PostMapping("/publish")
    public R<Article> publish(@Valid @RequestBody Article article, HttpServletRequest request) {
        // 防止 id 注入：JPA save() 遇到非空 id 会执行 update，可覆盖他人文章
        if (article.getId() != null) {
            throw new BusinessException("不允许指定文章 ID");
        }

        User user = currentUser(request);
        if (!permissionService.canPublish(user)) {
            throw new ForbiddenException("当前账号没有发布文章的权限，请联系管理员申请成为作者");
        }

        // 设置文章属性
        article.setAuthorId(user.getId());
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
    public R<List<Article>> list(@RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            // 兼容旧调用：不传分页参数时返回全量（前端当前依赖此行为）
            return R.ok(articleRepository.findByPublishedTrueOrderByCreateTimeDesc());
        }
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return R.ok(articleRepository.findByPublishedTrueOrderByCreateTimeDesc(pageable).getContent());
    }

    // ====== 我的文章（作者中心） ======
    @GetMapping("/mine")
    public R<List<Article>> mine(HttpServletRequest request) {
        User user = currentUser(request);
        return R.ok(articleRepository.findByAuthorIdOrderByCreateTimeDesc(user.getId()));
    }

    // ====== 文章详情（公开） ======
    @PublicApi
    @GetMapping("/{id}")
    public R<Article> detail(@PathVariable Long id) {
        var article = articleRepository.findByIdAndPublishedTrue(id);
        if (article.isEmpty()) {
            throw new BusinessException(404, "文章不存在或未发布");
        }

        // 原子自增，避免并发下读-改-写丢更新
        articleRepository.incrementViewCount(id);

        Article a = article.get();
        a.setViewCount(a.getViewCount() + 1);
        return R.ok(a);
    }

    // ====== 更新文章（作者本人或管理员） ======
    @PutMapping("/{id}")
    public R<Article> update(@PathVariable Long id, @Valid @RequestBody Article article, HttpServletRequest request) {
        User user = currentUser(request);

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!permissionService.canEditArticle(user, existing.get())) {
            throw new ForbiddenException("没有权限修改此文章");
        }

        Article a = existing.get();
        a.setTitle(article.getTitle());
        a.setContent(HtmlSanitizer.clean(article.getContent()));
        a.setSummary(article.getSummary());
        a.setCategory(article.getCategory());

        return R.ok(articleRepository.save(a));
    }

    // ====== 删除文章（作者本人或管理员） ======
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id, HttpServletRequest request) {
        User user = currentUser(request);

        var existing = articleRepository.findById(id);
        if (existing.isEmpty()) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!permissionService.canDeleteArticle(user, existing.get())) {
            throw new ForbiddenException("没有权限删除此文章");
        }

        articleRepository.deleteById(id);
        return R.ok("删除成功");
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute(AuthAttributes.CURRENT_USER);
    }
}
