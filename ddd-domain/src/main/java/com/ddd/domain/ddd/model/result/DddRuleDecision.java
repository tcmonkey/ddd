package com.ddd.domain.ddd.model.result;

import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 规则聚合完成计算后返回的领域决策。
 *
 * @param ruleCode 使用的规则编码
 * @param factor 规则计算因子
 * @param calculatedValue 计算后的领域值
 * @param reason 规则说明
 *
 * @author AIGenerator
 */
public record DddRuleDecision(
        String ruleCode,
        int factor,
        DddValue calculatedValue,
        String reason) {
}
