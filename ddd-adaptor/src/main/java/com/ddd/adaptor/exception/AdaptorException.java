package com.ddd.adaptor.exception;

import com.ddd.common.error.BaseException;

/**
 * Adaptor 私有协议转换或外部调用失败时抛出的异常。
 *
 * @author AIGenerator
 */
public class AdaptorException extends BaseException {
    /**
     * 创建并初始化 AdaptorException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     *
     * @author AIGenerator
     */
    public AdaptorException(AdaptorErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 创建并初始化 AdaptorException，校验或装配其所需输入。
     *
     * @param errorCode 本异常关联的模块错误码
     * @param cause 原始异常根因，用于保留可追溯堆栈
     *
     * @author AIGenerator
     */
    public AdaptorException(AdaptorErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
