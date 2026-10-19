package com.twilight.blog.dto.admin;

import jakarta.validation.constraints.NotNull;

/** 管理员上架 / 下架文章。 */
public record UpdatePublishedRequest(@NotNull(message = "发布状态不能为空") Boolean published) {
}
