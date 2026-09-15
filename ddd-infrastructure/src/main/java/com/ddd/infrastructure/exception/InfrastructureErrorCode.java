package com.ddd.infrastructure.exception;

import com.ddd.common.error.ErrorCode;

/**
 * 基础设施模块内部使用的错误码。
 *
 * @author AIGenerator
 */
public enum InfrastructureErrorCode implements ErrorCode {
    INFRASTRUCTURE_SNAPSHOT_SERIALIZE_FAILED("INFRASTRUCTURE_SNAPSHOT_SERIALIZE_FAILED", "数据快照保存失败"),
    INFRASTRUCTURE_SNAPSHOT_INVALID("INFRASTRUCTURE_SNAPSHOT_INVALID", "数据快照格式异常");

    private final String code;
    private final String message;

    InfrastructureErrorCode(String code, String message) {
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
