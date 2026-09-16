package com.ddd.domain.ddd.exception;

import com.ddd.common.error.ErrorCode;

/**
 * DDD 领域模块向上暴露的错误码。
 *
 * @author AIGenerator
 */
public enum DomainErrorCode implements ErrorCode {
    /**
     * 业务标识不合法对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_ID_INVALID("DOMAIN_ID_INVALID", "业务标识不合法"),
    /**
     * 领域数值不合法对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_VALUE_INVALID("DOMAIN_VALUE_INVALID", "领域数值不合法"),
    /**
     * 领域操作不合法对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_OPERATION_INVALID("DOMAIN_OPERATION_INVALID", "领域操作不合法"),
    /**
     * 领域规则不合法对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_RULE_INVALID("DOMAIN_RULE_INVALID", "领域规则不合法"),
    /**
     * 未找到可用领域规则对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_RULE_NOT_FOUND("DOMAIN_RULE_NOT_FOUND", "未找到可用领域规则"),
    /**
     * 数据已发生变更，请稍后重试对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_CONCURRENT_CONFLICT("DOMAIN_CONCURRENT_CONFLICT", "数据已发生变更，请稍后重试"),
    /**
     * 领域处理失败对应的模块错误码。
     *
     * @author AIGenerator
     */
    DOMAIN_PROCESS_FAILED("DOMAIN_PROCESS_FAILED", "领域处理失败");

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

    DomainErrorCode(String code, String message) {
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
