package com.ddd.client.ddd.request;

import jakarta.validation.constraints.Positive;

/**
 * DDD 纯计算模式的外部请求。
 *
 * @param baseValue 参与规则或纯计算的原始数值
 * @param factor 乘法计算因子，必须为正数
 *
 * @author AIGenerator
 */
public record DddCalculateRequest(@Positive int baseValue, @Positive int factor) {
}
