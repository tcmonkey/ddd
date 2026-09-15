package com.ddd.client.ddd.response;

/**
 * DDD 规则与计算模式的外部响应。
 *
 * @author AIGenerator
 */
public record DddRuleResponse(String ruleCode, int factor, int calculatedValue, String reason) {
}
