package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.exception.DomainValidationException;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DDD 写模式使用的聚合根模板。
 *
 * <p>所有状态变更均在此处完成，以保证当前值与实体明细保持同一业务不变量。</p>
 */
public final class DddAggregate {
    private final DddIdValue id;
    private DddValue currentValue;
    private long version;
    private final List<DddEntity> entities;

    private DddAggregate(DddIdValue id, DddValue currentValue, long version,
                                List<DddEntity> entities) {
        this.id = id;
        this.currentValue = currentValue;
        this.version = version;
        this.entities = new ArrayList<>(entities);
    }

    public static DddAggregate open(DddIdValue id) {
        return new DddAggregate(id, new DddValue(0), 0, List.of());
    }

    public static DddAggregate restore(DddIdValue id, DddValue currentValue, long version,
                                              List<DddEntity> entities) {
        return new DddAggregate(id, currentValue, version, entities);
    }

    public void write(DddOperationIdValue operationId, DddValue value, String ruleCode, Instant occurredAt) {
        if (findEntity(operationId).isPresent()) {
            throw new DomainValidationException("duplicate operationId: " + operationId.value());
        }
        currentValue = currentValue.add(value);
        entities.add(new DddEntity(operationId, value, ruleCode, occurredAt));
        version++;
    }

    public Optional<DddEntity> findEntity(DddOperationIdValue operationId) {
        return entities.stream().filter(item -> item.operationId().equals(operationId)).findFirst();
    }

    public DddIdValue id() {
        return id;
    }

    public DddValue currentValue() {
        return currentValue;
    }

    public long version() {
        return version;
    }

    public List<DddEntity> entities() {
        return List.copyOf(entities);
    }

    public DddAggregate copy() {
        return restore(id, currentValue, version, entities);
    }
}
