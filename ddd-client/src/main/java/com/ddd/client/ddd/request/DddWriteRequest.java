package com.ddd.client.ddd.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DDD 写模式的外部请求。
 *
 * @param id 业务对象标识
 * @param operationId 一次写操作的幂等标识
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddWriteRequest(
        @NotBlank String id,
        @NotBlank String operationId,
        @NotBlank String ruleCode,
        @Positive int baseValue) {
}
