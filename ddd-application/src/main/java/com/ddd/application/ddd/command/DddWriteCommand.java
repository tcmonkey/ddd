package com.ddd.application.ddd.command;

/**
 * DDD 写模式的应用层输入参数。
 *
 * <p>该对象与 client 请求对象隔离，用于保护应用用例契约。</p>
 *
 * @author AIGenerator
 */
public record DddWriteCommand(String id, String operationId, String ruleCode, int baseValue) {
}
