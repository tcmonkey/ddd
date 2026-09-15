package com.ddd.client.common;

/**
 * 对外协议的统一响应包装。
 *
 * <p>该对象属于 client 模块而非 model，调用方不应依赖内部领域模型或持久化模型。</p>
 *
 * @param success 调用是否成功
 * @param code 对外结果编码
 * @param message 对外结果说明
 * @param data 响应数据
 *
 * @author AIGenerator
 */
public record Result<T>(
        boolean success,
        String code,
        String message,
        T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "success", data);
    }

    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(false, code, message, null);
    }
}
