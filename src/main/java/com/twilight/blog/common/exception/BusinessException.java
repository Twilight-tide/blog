package com.twilight.blog.common.exception;

/**
 * 业务规则错误（用户输入导致）。默认返回 400，可指定其它 HTTP 状态码。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
