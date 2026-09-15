package com.ddd.client.ddd.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** DDD 写模式的外部请求。 */
public record DddWriteRequest(
        @NotBlank String id,
        @NotBlank String operationId,
        @NotBlank String ruleCode,
        @Positive int baseValue) {
}
