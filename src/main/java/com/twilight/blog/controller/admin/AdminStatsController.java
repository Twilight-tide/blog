package com.twilight.blog.controller.admin;

import com.twilight.blog.common.result.R;
import com.twilight.blog.dto.admin.StatsDto;
import com.twilight.blog.entity.UserRole;
import com.twilight.blog.repository.ArticleRepository;
import com.twilight.blog.repository.CommentRepository;
import com.twilight.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台首页统计。
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @GetMapping
    public R<StatsDto> stats() {
        return R.ok(new StatsDto(
                userRepository.count(),
                userRepository.countByRole(UserRole.ADMIN),
                userRepository.countByRole(UserRole.AUTHOR),
                userRepository.countByEnabledFalse(),
                articleRepository.count(),
                articleRepository.countByPublishedTrue(),
                commentRepository.count()
        ));
    }
}
