package com.ddd.client.ddd.response;

/** DDD 写模式执行或幂等重放后的外部响应。 */
public record DddWriteResponse(String id, String operationId, int changedValue,
                                        int currentValue, boolean duplicate) {
}
