package com.ddd.domain.ddd.model.entity;

import java.time.Instant;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 由 {@link com.ddd.domain.ddd.model.aggregate.DddAggregate} 持有的领域实体模板。
 *
 * @param operationId 一次写操作的幂等标识
 * @param value 本次实体记录的领域数值
 * @param ruleCode 选择或记录规则的编码
 * @param occurredAt 本次操作的发生时间
 *
 * @author AIGenerator
 */
public record DddEntity(
        DddOperationIdValue operationId,
        DddValue value,
        String ruleCode,
        Instant occurredAt) {
    public DddEntity {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("ruleCode must not be blank");
        }
    }
}
