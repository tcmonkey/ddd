package com.ddd.domain.ddd.exception;

import com.ddd.common.error.BaseException;

/**
 * 领域模型或领域服务抛出的语义异常。
 *
 * <p>该异常不携带 HTTP 或第三方协议信息，由领域服务公开方法转换成统一结果。</p>
 *
 * @author AIGenerator
 */
public class DomainException extends BaseException {
    /**
     * 创建并初始化 DomainException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     *
     * @author AIGenerator
     */
    public DomainException(DomainErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 创建并初始化 DomainException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     * @param cause 原始异常根因，用于保留可追溯堆栈
     *
     * @author AIGenerator
     */
    public DomainException(DomainErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
