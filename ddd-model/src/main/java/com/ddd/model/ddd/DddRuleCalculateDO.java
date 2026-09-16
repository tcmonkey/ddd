package com.ddd.model.ddd;

/**
 * 规则领域服务向 Application 返回的内部数据对象。
 *
 * @param ruleCode 规则编码
 * @param factor 规则计算因子
 * @param calculatedValue 计算结果
 * @param reason 规则说明
 *
 * @author AIGenerator
 */
public record DddRuleCalculateDO(
        String ruleCode,
        int factor,
        int calculatedValue,
        String reason) {
}
