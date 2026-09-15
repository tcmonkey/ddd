package com.ddd.application.exception;

import com.ddd.common.error.ErrorCode;

/**
 * 应用层编排或结果转换失败时使用的错误码。
 *
 * @author AIGenerator
 */
public enum ApplicationErrorCode implements ErrorCode {
    APPLICATION_PROCESS_FAILED("APPLICATION_PROCESS_FAILED", "应用处理失败");

    private final String code;
    private final String message;

    ApplicationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
