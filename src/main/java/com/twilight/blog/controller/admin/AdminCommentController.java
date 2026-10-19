package com.twilight.blog.controller.admin;

import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.result.PageResult;
import com.twilight.blog.common.result.R;
import com.twilight.blog.common.util.PageRequests;
import com.twilight.blog.dto.admin.CommentAdminDto;
import com.twilight.blog.entity.Article;
import com.twilight.blog.entity.Comment;
import com.twilight.blog.entity.User;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.CommentRepository;
import com.twilight.blog.repository.UserRepository;
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
 * 后台评论管理：列表、删除。
 */
@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @GetMapping
    public R<PageResult<CommentAdminDto>> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) Long articleId) {
        Pageable pageable = PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Comment> result = articleId == null
                ? commentRepository.findAll(pageable)
                : commentRepository.findByArticleId(articleId, pageable);

        Map<Long, String> usernames = usernames(result.getContent());
        Map<Long, String> titles = articleTitles(result.getContent());
        Page<CommentAdminDto> mapped = result.map(c -> CommentAdminDto.from(
                c, usernames.get(c.getUserId()), titles.get(c.getArticleId())));
        return R.ok(PageResult.ofOneBased(mapped));
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            throw new BusinessException(404, "评论不存在");
        }
        commentRepository.deleteById(id);
        return R.ok("删除成功");
    }

    private Map<Long, String> usernames(List<Comment> comments) {
        Set<Long> ids = comments.stream()
                .map(Comment::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    private Map<Long, String> articleTitles(List<Comment> comments) {
        Set<Long> ids = comments.stream()
                .map(Comment::getArticleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return articleRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Article::getId, Article::getTitle));
    }
}
