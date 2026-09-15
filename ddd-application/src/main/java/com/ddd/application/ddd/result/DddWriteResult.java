package com.ddd.application.ddd.result;

/** DDD 写模式的应用层结果，由适配层转换为 client 响应。 */
public record DddWriteResult(String id, String operationId, int changedValue,
                                      int currentValue, boolean duplicate) {
}
