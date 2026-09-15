package com.ddd.client.ddd.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DDD 规则与计算模式的外部请求。
 *
 * @author AIGenerator
 */
public record DddRuleRequest(@NotBlank String ruleCode, @Positive int baseValue) {
}
