package com.ddd.client.ddd.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DDD 规则与计算模式的外部请求。
 *
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddRuleRequest(@NotBlank String ruleCode, @Positive int baseValue) {
}
