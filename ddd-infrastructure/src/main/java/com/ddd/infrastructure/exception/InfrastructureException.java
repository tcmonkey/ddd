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
    /**
     * 创建并初始化 InfrastructureException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     *
     * @author AIGenerator
     */
    public InfrastructureException(InfrastructureErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 创建并初始化 InfrastructureException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     * @param cause 原始异常根因，用于保留可追溯堆栈
     *
     * @author AIGenerator
     */
    public InfrastructureException(InfrastructureErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
