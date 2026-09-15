package com.ddd.application.ddd.command;

/**
 * DDD 写模式的应用层输入参数。
 *
 * <p>该对象与 client 请求对象隔离，用于保护应用用例契约。</p>
 *
 * @param id 业务对象标识
 * @param operationId 一次写操作的幂等标识
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddWriteCommand(
        String id,
        String operationId,
        String ruleCode,
        int baseValue) {
}
