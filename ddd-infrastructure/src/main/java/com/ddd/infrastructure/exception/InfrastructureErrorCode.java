package com.ddd.infrastructure.exception;

import com.ddd.common.error.ErrorCode;

/**
 * 基础设施模块内部使用的错误码。
 *
 * @author AIGenerator
 */
public enum InfrastructureErrorCode implements ErrorCode {
    /**
     * 数据快照保存失败对应的模块错误码。
     *
     * @author AIGenerator
     */
    INFRASTRUCTURE_SNAPSHOT_SERIALIZE_FAILED("INFRASTRUCTURE_SNAPSHOT_SERIALIZE_FAILED", "数据快照保存失败"),
    /**
     * 数据快照格式异常对应的模块错误码。
     *
     * @author AIGenerator
     */
    INFRASTRUCTURE_SNAPSHOT_INVALID("INFRASTRUCTURE_SNAPSHOT_INVALID", "数据快照格式异常");

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

    InfrastructureErrorCode(String code, String message) {
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
