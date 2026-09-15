package com.ddd.application.ddd.result;

/**
 * DDD 规则与计算模式的应用层结果。
 *
 * @author AIGenerator
 */
public record DddRuleResult(String ruleCode, int factor, int calculatedValue, String reason) {
}
