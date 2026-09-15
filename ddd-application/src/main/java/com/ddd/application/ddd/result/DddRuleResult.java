package com.ddd.application.ddd.result;

/**
 * DDD 规则与计算模式的应用层结果。
 *
 * @param ruleCode 选择或记录规则的编码
 * @param factor 乘法计算因子，必须为正数
 * @param calculatedValue 规则或纯计算得到的数值
 * @param reason 规则或领域决策的说明
 *
 * @author AIGenerator
 */
public record DddRuleResult(
        String ruleCode,
        int factor,
        int calculatedValue,
        String reason) {
}
