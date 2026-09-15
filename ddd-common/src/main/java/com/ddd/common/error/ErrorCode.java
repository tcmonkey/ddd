package com.ddd.common.error;

/**
 * 各分层错误码的统一契约。
 *
 * <p>错误码由产生错误的模块枚举定义，返回结构只保存稳定的编码和对外友好文案，
 * 不暴露异常堆栈或
 * 下游原始报文。</p>
 *
 * @author AIGenerator
 */
public interface ErrorCode {
    /**
     * 获取稳定的项目内部错误编码。
     *
     * @return 错误编码
     *
     * @author AIGenerator
     */
    String code();

    /**
     * 获取可安全返回给上层的默认错误文案。
     *
     * @return 默认错误文案
     *
     * @author AIGenerator
     */
    String message();
}
