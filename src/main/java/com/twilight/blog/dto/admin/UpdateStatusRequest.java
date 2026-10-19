package com.twilight.blog.dto.admin;

import jakarta.validation.constraints.NotNull;

/** 管理员启用 / 封禁账号。 */
public record UpdateStatusRequest(@NotNull(message = "启用状态不能为空") Boolean enabled) {
}
