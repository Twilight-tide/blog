package com.twilight.blog.common.exception;

/**
 * 已登录但权限不足（403）。
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}
