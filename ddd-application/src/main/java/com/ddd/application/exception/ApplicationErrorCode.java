package com.ddd.application.exception;

import com.ddd.common.error.ErrorCode;

/**
 * 应用层编排或结果转换失败时使用的错误码。
 *
 * @author AIGenerator
 */
public enum ApplicationErrorCode implements ErrorCode {
    /**
     * 应用处理失败对应的模块错误码。
     *
     * @author AIGenerator
     */
    APPLICATION_PROCESS_FAILED("APPLICATION_PROCESS_FAILED", "应用处理失败");

    /**
     * 稳定的项目内部错误编码。
     *
     * @author AIGenerator
     */
    private final String code;
    /**
     * 向上层暴露的友好错误说明。
     *
     * @author AIGenerator
     */
    private final String message;

    ApplicationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取稳定的项目内部错误编码。
     *
     * @return 项目内部错误编码
     *
     * @author AIGenerator
     */
    @Override
    public String code() {
        return code;
    }

    /**
     * 获取向上层暴露的友好错误说明。
     *
     * @return 友好错误说明
     *
     * @author AIGenerator
     */
    @Override
    public String message() {
        return message;
    }
}
