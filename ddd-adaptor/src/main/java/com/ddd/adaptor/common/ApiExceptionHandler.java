package com.ddd.adaptor.common;

import com.ddd.client.common.Result;
import com.ddd.domain.ddd.exception.DomainValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ConcurrentModificationException;

/**
 * HTTP 异常统一映射器。
 *
 * <p>领域异常不依赖 Web 框架，由输入适配层统一转换为外部协议响应。</p>
 *
 * @author AIGenerator
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({DomainValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<Result<Void>> handleBusinessError(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Result.failure("VALIDATION_ERROR", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleRequestValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("invalid request");
        return ResponseEntity.badRequest().body(Result.failure("REQUEST_INVALID", message));
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<Result<Void>> handleConcurrencyConflict(ConcurrentModificationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Result.failure("CONCURRENT_CONFLICT", exception.getMessage()));
    }
}
