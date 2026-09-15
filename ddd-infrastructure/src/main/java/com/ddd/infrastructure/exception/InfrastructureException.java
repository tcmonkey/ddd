package com.ddd.infrastructure.exception;

import com.ddd.common.error.BaseException;

/**
 * 基础设施私有处理失败时抛出的异常。
 *
 * <p>仓储只补充持久化上下文后继续抛出，不记录日志也不直接构造 Result。</p>
 *
 * @author AIGenerator
 */
public class InfrastructureException extends BaseException {
    public InfrastructureException(InfrastructureErrorCode errorCode) {
        super(errorCode);
    }

    public InfrastructureException(InfrastructureErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
