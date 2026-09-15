package com.ddd.client.ddd.request;

import jakarta.validation.constraints.Positive;

/**
 * DDD 纯计算模式的外部请求。
 *
 * @author AIGenerator
 */
public record DddCalculateRequest(@Positive int baseValue, @Positive int factor) {
}
