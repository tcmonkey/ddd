package com.ddd.common.error;

import com.ddd.common.result.Result;

/**
 * 为各层入口捕获的异常提供统一安全分类，不承担调用或事务边界。
 *
 * @author AIGenerator
 */
public final class Failures {
    private Failures() {
}

    /**
     * 保留已分类业务异常，并将未知异常转换为本层稳定错误。
     *
     * @param exception 本层入口捕获的异常
     * @param fallback 未知异常对应的本层错误
     * @param <T> 成功载荷类型
     * @return 不含技术详情的失败结果
     * @author AIGenerator
     */
    public static <T> Result<T> capture(Exception exception, ErrorCode fallback) {
        // 1. 保留业务异常已有的错误分类，不被兜底码覆盖。
        if (exception instanceof BaseException business) {
            return Result.failure(business.errorCode());
        }
        // 2. 未知异常只返回稳定兜底码，不泄漏原始异常消息。
        return Result.failure(fallback);
    }
}
