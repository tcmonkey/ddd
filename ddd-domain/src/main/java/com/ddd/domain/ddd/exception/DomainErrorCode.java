package com.ddd.domain.ddd.exception;

import com.ddd.common.error.ErrorCode;

/**
 * DDD 领域模块向上暴露的错误码。
 *
 * @author AIGenerator
 */
public enum DomainErrorCode implements ErrorCode {
    DOMAIN_ID_INVALID("DOMAIN_ID_INVALID", "业务标识不合法"),
    DOMAIN_VALUE_INVALID("DOMAIN_VALUE_INVALID", "领域数值不合法"),
    DOMAIN_OPERATION_INVALID("DOMAIN_OPERATION_INVALID", "领域操作不合法"),
    DOMAIN_RULE_INVALID("DOMAIN_RULE_INVALID", "领域规则不合法"),
    DOMAIN_RULE_NOT_FOUND("DOMAIN_RULE_NOT_FOUND", "未找到可用领域规则"),
    DOMAIN_CONCURRENT_CONFLICT("DOMAIN_CONCURRENT_CONFLICT", "数据已发生变更，请稍后重试"),
    DOMAIN_PROCESS_FAILED("DOMAIN_PROCESS_FAILED", "领域处理失败");

    private final String code;
    private final String message;

    DomainErrorCode(String code, String message) {
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
