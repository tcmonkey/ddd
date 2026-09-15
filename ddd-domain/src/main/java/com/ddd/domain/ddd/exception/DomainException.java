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
    public DomainException(DomainErrorCode errorCode) {
        super(errorCode);
    }

    public DomainException(DomainErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
