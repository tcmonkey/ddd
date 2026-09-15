package com.ddd.domain.ddd.model.entity;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

import java.time.Instant;

/** 由 {@link com.ddd.domain.ddd.model.aggregate.DddAggregate} 持有的领域实体模板。 */
public record DddEntity(DddOperationIdValue operationId, DddValue value, String ruleCode, Instant occurredAt) {
    public DddEntity {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("ruleCode must not be blank");
        }
    }
}
