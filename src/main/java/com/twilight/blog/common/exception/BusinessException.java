package com.twilight.blog.common.exception;

/**
 * 业务规则错误（用户输入导致，应返回 400 而不是 500）。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}