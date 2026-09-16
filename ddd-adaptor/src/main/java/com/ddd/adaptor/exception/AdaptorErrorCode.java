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
    /**
     * 请求参数不合法对应的模块错误码。
     *
     * @author AIGenerator
     */
    ADAPTOR_REQUEST_INVALID("ADAPTOR_REQUEST_INVALID", "请求参数不合法"),
    /**
     * 外部服务调用超时对应的模块错误码。
     *
     * @author AIGenerator
     */
    ADAPTOR_EXTERNAL_TIMEOUT("ADAPTOR_EXTERNAL_TIMEOUT", "外部服务调用超时"),
    /**
     * 外部服务拒绝处理对应的模块错误码。
     *
     * @author AIGenerator
     */
    ADAPTOR_EXTERNAL_REJECTED("ADAPTOR_EXTERNAL_REJECTED", "外部服务拒绝处理"),
    /**
     * 外部服务响应异常对应的模块错误码。
     *
     * @author AIGenerator
     */
    ADAPTOR_EXTERNAL_RESPONSE_INVALID("ADAPTOR_EXTERNAL_RESPONSE_INVALID", "外部服务响应异常"),
    /**
     * 适配处理失败对应的模块错误码。
     *
     * @author AIGenerator
     */
    ADAPTOR_PROCESS_FAILED("ADAPTOR_PROCESS_FAILED", "适配处理失败");

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

    AdaptorErrorCode(String code, String message) {
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
