package com.ddd.adaptor.common;

import com.ddd.adaptor.exception.AdaptorErrorCode;
import com.ddd.common.error.BaseException;
import com.ddd.common.error.Failures;
import com.ddd.common.result.Result;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 输入入口异常的安全转换，不代替Controller自身的try-catch。
 *
 * @author AIGenerator
 */
public final class HttpResults {
    private HttpResults() {
    }

    /**
     * 保留标准错误并隔离未知故障中的堆栈与原始消息。
     *
     * @param exception 入口捕获的异常
     * @param <T> 对外载荷类型
     * @return 安全失败结果
     * @author AIGenerator
     */
    public static <T> Result<T> capture(Exception exception) {
        if (RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes) {
            var response = attributes.getResponse();
            if (response != null && !response.isCommitted()) {
                response.setStatus(exception instanceof BaseException ? 400 : 500);
            }
        }
        return Failures.capture(exception, AdaptorErrorCode.ADAPTOR_PROCESS_FAILED);
    }
}
