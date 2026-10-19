package com.twilight.blog.controller;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.exception.ForbiddenException;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Comment;
import com.twilight.blog.entity.User;
import com.twilight.blog.repository.CommentRepository;
import com.twilight.blog.service.PermissionService;
import com.twilight.blog.utils.HtmlSanitizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final PermissionService permissionService;

    // ====== 获取某篇文章的所有评论（公开） ======
    @PublicApi
    @GetMapping("/list/{articleId}")
    public R<List<Comment>> list(@PathVariable Long articleId) {
        // 先查顶级评论
        List<Comment> topComments = commentRepository.findByArticleIdAndParentIdOrderByCreateTimeDesc(articleId, 0L);
        // 这里暂不实现嵌套回复，先只返回一级评论
        return R.ok(topComments);
    }

    // ====== 发表评论（需登录，任何角色都可以） ======
    @PostMapping("/publish")
    public R<Comment> publish(@Valid @RequestBody Comment comment, HttpServletRequest request) {
        // 防止 id 注入：JPA save() 遇到非空 id 会执行 update，可覆盖他人评论
        if (comment.getId() != null) {
            throw new BusinessException("不允许指定评论 ID");
        }

        User user = currentUser(request);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }

        comment.setUserId(user.getId());
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }

        comment.setContent(HtmlSanitizer.clean(comment.getContent()));
        return R.ok(commentRepository.save(comment));
    }

    // ====== 删除评论（评论作者本人或管理员） ======
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id, HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }

        var comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new BusinessException(404, "评论不存在");
        }

        if (!permissionService.canDeleteComment(user, comment.get())) {
            throw new ForbiddenException("没有权限删除此评论");
        }

        commentRepository.deleteById(id);
        return R.ok("删除成功");
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute(AuthAttributes.CURRENT_USER);
    }
}
