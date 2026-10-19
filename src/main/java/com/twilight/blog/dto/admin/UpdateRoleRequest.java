package com.twilight.blog.dto.admin;

import com.twilight.blog.entity.UserRole;
import jakarta.validation.constraints.NotNull;

/** 管理员修改用户角色。 */
public record UpdateRoleRequest(@NotNull(message = "角色不能为空") UserRole role) {
}
