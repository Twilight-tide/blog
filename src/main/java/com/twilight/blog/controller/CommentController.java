package com.twilight.blog.controller;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.Comment;
import com.twilight.blog.repository.CommentRepository;
import com.twilight.blog.repository.UserRepository;
import com.twilight.blog.utils.JwtUtil;
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
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // ====== 获取某篇文章的所有评论（公开） ======
    @PublicApi
    @GetMapping("/list/{articleId}")
    public R<List<Comment>> list(@PathVariable Long articleId) {
        // 先查顶级评论
        List<Comment> topComments = commentRepository.findByArticleIdAndParentIdOrderByCreateTimeDesc(articleId, 0L);
        // 这里暂不实现嵌套回复，先只返回一级评论
        return R.ok(topComments);
    }

    // ====== 发表评论（需登录） ======
    @PostMapping("/publish")
    public R<Comment> publish(@Valid @RequestBody Comment comment, HttpServletRequest request) {
        // 获取当前登录用户
        String username = (String) request.getAttribute("username");
        if (username == null || username.isEmpty()) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    username = jwtUtil.extractUsername(token);
                } catch (Exception e) {
                    return R.error("Token 无效");
                }
            }
        }

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return R.error("用户不存在");
        }

        comment.setUserId(user.get().getId());
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }

        Comment saved = commentRepository.save(comment);
        return R.ok(saved);
    }

    // ====== 删除评论（仅作者或博主） ======
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null || username.isEmpty()) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    username = jwtUtil.extractUsername(token);
                } catch (Exception e) {
                    return R.error("Token 无效");
                }
            }
        }

        if (username == null || username.isEmpty()) {
            return R.error("请先登录");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return R.error("用户不存在");
        }

        var comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            return R.error("评论不存在");
        }

        // 只有博主（twilight）或评论作者本人可以删除
        if (!user.get().getUsername().equals("twilight") && !user.get().getId().equals(comment.get().getUserId())) {
            return R.error("没有权限删除此评论");
        }

        commentRepository.deleteById(id);
        return R.ok("删除成功");
    }
}