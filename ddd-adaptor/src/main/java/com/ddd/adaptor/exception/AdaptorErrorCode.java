package com.ddd.adaptor.exception;

import com.ddd.common.error.ErrorCode;

/**
 * Adaptor 模块对上层暴露的错误码。
 *
 * <p>外部系统原始错误码仅写入日志；本枚举提供稳定的项目内部错误码。</p>
 *
 * @author AIGenerator
 */
public enum AdaptorErrorCode implements ErrorCode {
    ADAPTOR_REQUEST_INVALID("ADAPTOR_REQUEST_INVALID", "请求参数不合法"),
    ADAPTOR_EXTERNAL_TIMEOUT("ADAPTOR_EXTERNAL_TIMEOUT", "外部服务调用超时"),
    ADAPTOR_EXTERNAL_REJECTED("ADAPTOR_EXTERNAL_REJECTED", "外部服务拒绝处理"),
    ADAPTOR_EXTERNAL_RESPONSE_INVALID("ADAPTOR_EXTERNAL_RESPONSE_INVALID", "外部服务响应异常"),
    ADAPTOR_PROCESS_FAILED("ADAPTOR_PROCESS_FAILED", "适配处理失败");

    private final String code;
    private final String message;

    AdaptorErrorCode(String code, String message) {
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
