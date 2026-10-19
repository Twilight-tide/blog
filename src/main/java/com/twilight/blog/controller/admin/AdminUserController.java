package com.twilight.blog.controller.admin;

import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.common.exception.BusinessException;
import com.twilight.blog.common.result.PageResult;
import com.twilight.blog.common.result.R;
import com.twilight.blog.common.util.PageRequests;
import com.twilight.blog.common.util.PasswordGenerator;
import com.twilight.blog.common.util.Texts;
import com.twilight.blog.dto.admin.UpdateRemarkRequest;
import com.twilight.blog.dto.admin.UpdateRoleRequest;
import com.twilight.blog.dto.admin.UpdateStatusRequest;
import com.twilight.blog.entity.User;
import com.twilight.blog.entity.UserRole;
import com.twilight.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台账号管理：列表、改角色、封禁/解封、备注、重置密码。
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public R<PageResult<User>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) UserRole role) {
        Pageable pageable = PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<User> result = userRepository.search(Texts.blankToNull(keyword), role, pageable);
        return R.ok(PageResult.ofOneBased(result));
    }

    @PatchMapping("/{id}/role")
    public R<User> updateRole(@PathVariable Long id,
                              @Valid @RequestBody UpdateRoleRequest body,
                              HttpServletRequest request) {
        User me = currentUser(request);
        User target = getTarget(id);

        if (target.getId().equals(me.getId())) {
            throw new BusinessException("不能修改自己的角色");
        }
        // 防止把最后一名管理员降级，导致无人能进后台
        if (target.getRole() == UserRole.ADMIN
                && body.role() != UserRole.ADMIN
                && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new BusinessException("系统必须保留至少一名管理员");
        }

        target.setRole(body.role());
        return R.ok(userRepository.save(target));
    }

    @PatchMapping("/{id}/status")
    public R<User> updateStatus(@PathVariable Long id,
                                @Valid @RequestBody UpdateStatusRequest body,
                                HttpServletRequest request) {
        User me = currentUser(request);
        User target = getTarget(id);

        if (target.getId().equals(me.getId())) {
            throw new BusinessException("不能封禁自己");
        }
        if (Boolean.FALSE.equals(body.enabled()) && target.getRole() == UserRole.ADMIN) {
            throw new BusinessException("不能封禁管理员账号");
        }

        target.setEnabled(body.enabled());
        return R.ok(userRepository.save(target));
    }

    @PatchMapping("/{id}/remark")
    public R<User> updateRemark(@PathVariable Long id,
                                @Valid @RequestBody UpdateRemarkRequest body) {
        User target = getTarget(id);
        target.setRemark(Texts.blankToNull(body.remark()));
        return R.ok(userRepository.save(target));
    }

    /** 生成随机临时密码，明文只在本次响应里返回一次。 */
    @PostMapping("/{id}/reset-password")
    public R<Map<String, String>> resetPassword(@PathVariable Long id) {
        User target = getTarget(id);
        String raw = PasswordGenerator.random();
        target.setPassword(passwordEncoder.encode(raw));
        userRepository.save(target);
        return R.ok(Map.of("password", raw));
    }

    private User getTarget(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute(AuthAttributes.CURRENT_USER);
    }
}
