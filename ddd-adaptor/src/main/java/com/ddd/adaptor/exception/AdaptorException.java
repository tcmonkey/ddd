package com.ddd.adaptor.exception;

import com.ddd.common.error.BaseException;

/**
 * Adaptor 私有协议转换或外部调用失败时抛出的异常。
 *
 * @author AIGenerator
 */
public class AdaptorException extends BaseException {
    public AdaptorException(AdaptorErrorCode errorCode) {
        super(errorCode);
    }

    public AdaptorException(AdaptorErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
