package com.twilight.blog.controller.admin;

import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.result.PageResult;
import com.twilight.blog.common.result.R;
import com.twilight.blog.common.util.PageRequests;
import com.twilight.blog.common.util.Texts;
import com.twilight.blog.dto.admin.ArticleAdminDto;
import com.twilight.blog.dto.admin.UpdatePublishedRequest;
import com.twilight.blog.entity.Article;
import com.twilight.blog.entity.User;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 后台内容管理：文章列表（含未发布）、上架/下架、删除。
 */
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AdminService adminService;

    @GetMapping
    public R<PageResult<ArticleAdminDto>> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Boolean published) {
        Pageable pageable = PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> result = articleRepository.search(Texts.blankToNull(keyword), published, pageable);
        Map<Long, String> authorNames = authorNames(result.getContent());
        Page<ArticleAdminDto> mapped = result.map(a -> ArticleAdminDto.from(a, authorNames.get(a.getAuthorId())));
        return R.ok(PageResult.ofOneBased(mapped));
    }

    /** 后台查看单篇文章完整内容（含未发布），供编辑对话框使用。 */
    @GetMapping("/{id}")
    public R<Article> detail(@PathVariable Long id) {
        return R.ok(articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在")));
    }

    @PatchMapping("/{id}/status")
    public R<Article> updatePublished(@PathVariable Long id,
                                      @Valid @RequestBody UpdatePublishedRequest body) {        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在"));
        article.setPublished(body.published());
        return R.ok(articleRepository.save(article));
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        if (!articleRepository.existsById(id)) {
            throw new BusinessException(404, "文章不存在");
        }
        adminService.deleteArticleWithComments(id);
        return R.ok("删除成功");
    }

    private Map<Long, String> authorNames(List<Article> articles) {
        Set<Long> ids = articles.stream()
                .map(Article::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }
}
