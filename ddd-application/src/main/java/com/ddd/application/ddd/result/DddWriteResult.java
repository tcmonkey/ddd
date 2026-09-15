package com.ddd.application.ddd.result;

/**
 * DDD 写模式的应用层结果，由适配层转换为 client 响应。
 *
 * @param id 业务对象标识
 * @param operationId 一次写操作的幂等标识
 * @param changedValue 本次写入使聚合根变化的数值
 * @param currentValue 聚合根当前数值
 * @param duplicate 是否为幂等重放
 *
 * @author AIGenerator
 */
public record DddWriteResult(
        String id,
        String operationId,
        int changedValue,
        int currentValue,
        boolean duplicate) {
}
