package com.ddd.common.error;

/**
 * 分层业务异常的公共基类。
 *
 * <p>异常仅在本层公开调用入口被记录并转换为 {@code Result.failure}；
 * 私有方法使用它携带错误码和
 * 根因后继续向上抛出，不重复打印日志。</p>
 *
 * @author AIGenerator
 */
public abstract class BaseException extends RuntimeException {
    /**
     * 本异常关联的模块错误码。
     *
     * @author AIGenerator
     */
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    protected BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取异常对应的分层错误码。
     *
     * @return 错误码枚举
     *
     * @author AIGenerator
     */
    public ErrorCode errorCode() {
        return errorCode;
    }
}
