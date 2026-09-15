package com.ddd.domain.ddd.exception;

/**
 * 领域校验异常。
 *
 * <p>异常到 HTTP 错误响应的转换由适配层决定，领域层不依赖 Web 框架。</p>
 *
 * @author AIGenerator
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
