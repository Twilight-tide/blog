package com.twilight.blog.dto.admin;

import jakarta.validation.constraints.Size;

/** 管理员修改账号备注。 */
public record UpdateRemarkRequest(
        @Size(max = 200, message = "备注不能超过 200 个字符") String remark) {
}
