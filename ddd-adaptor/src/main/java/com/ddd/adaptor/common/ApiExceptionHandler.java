package com.ddd.adaptor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ddd.adaptor.exception.AdaptorErrorCode;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * HTTP 异常统一映射器。
 *
 * <p>领域异常不依赖 Web 框架，由输入适配层统一转换为外部协议响应。</p>
 *
 * @author AIGenerator
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 兜底处理未在领域服务公开入口转换的领域异常。
     *
     * @param exception 领域异常
     * @return HTTP 400 与统一失败结果
     *
     * @author AIGenerator
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Result<Void>> handleDomainFallback(DomainException exception) {
        LOG.warn("未在领域服务入口转换的领域异常, code={}", exception.errorCode().code());
        return ResponseEntity.badRequest().body(Result.failure(exception.errorCode()));
    }

    /**
     * 将 HTTP 请求格式校验失败转换为输入适配层错误码。
     *
     * @param exception 请求校验异常
     * @return HTTP 400 与统一失败结果
     *
     * @author AIGenerator
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleRequestValidation(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(Result.failure(AdaptorErrorCode.ADAPTOR_REQUEST_INVALID));
    }

    /**
     * 记录并隔离未被任何主调用边界处理的异常。
     *
     * @param exception 未预期异常
     * @return HTTP 500 与统一失败结果
     *
     * @author AIGenerator
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpectedException(Exception exception) {
        LOG.error("未被主调用链处理的 HTTP 异常", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(AdaptorErrorCode.ADAPTOR_PROCESS_FAILED));
    }
}
