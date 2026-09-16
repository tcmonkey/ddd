package com.ddd.common.result;

import java.util.function.Function;

import com.ddd.common.error.ErrorCode;

/**
 * 各模块共用的操作结果包装。
 *
 * <p>该类型不包含 HTTP 状态、第三方原始错误或技术堆栈，因此可以在 domain、application、
 * infrastructure、adaptor 和 client 协议之间安全复用。</p>
 *
 * @param success 操作是否成功
 * @param code 稳定的项目内部结果编码
 * @param message 可向上层暴露的友好说明
 * @param data 成功时的数据；失败时为空
 *
 * @param <T> 成功结果的数据类型
 * @author AIGenerator
 */
public record Result<T>(
        boolean success,
        String code,
        String message,
        T data) {
    /**
     * 创建成功结果。
     *
     * @param data 成功数据
     * @param <T> 数据类型
     * @return 成功结果
     *
     * @author AIGenerator
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "success", data);
    }

    /**
     * 根据分层错误码创建失败结果。
     *
     * @param errorCode 产生失败的模块错误码
     * @param <T> 数据类型
     * @return 失败结果
     *
     * @author AIGenerator
     */
    public static <T> Result<T> failure(ErrorCode errorCode) {
        return failure(errorCode.code(), errorCode.message());
    }

    /**
     * 根据已标准化的错误码和文案创建失败结果。
     *
     * @param code 项目内部错误编码
     * @param message 友好错误文案
     * @param <T> 数据类型
     * @return 失败结果
     *
     * @author AIGenerator
     */
    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(false, code, message, null);
    }

    /**
     * 在成功时转换数据，失败时保持错误码和文案不变。
     *
     * @param mapper 成功数据转换函数
     * @param <R> 转换后的数据类型
     * @return 转换后的结果
     *
     * @author AIGenerator
     */
    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (!success) {
            return Result.failure(code, message);
        }
        return Result.success(mapper.apply(data));
    }
}
