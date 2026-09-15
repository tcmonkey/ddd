package com.ddd.domain.ddd.model.aggregate;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.ddd.domain.ddd.exception.DomainValidationException;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * DDD 聚合根状态变更与幂等边界测试。
 *
 * @author AIGenerator
 */
class DddAggregateTest {
    @Test
    void shouldChangeRootEntityAndCreateOperationEntityTogether() {
        DddEntity entity = DddEntity.open(new DddIdValue("ddd-001"));
        DddOperationEntity operation = DddOperationEntity.pending(
                new DddOperationIdValue("operation-001"), DddValue.positive(12), "DEFAULT");

        entity.confirm(operation, DddValue.positive(12), Instant.parse("2026-09-14T00:00:00Z"));

        assertEquals(12, entity.currentValue().value());
        assertEquals(1, entity.operationEntities().size());
        assertEquals(1, entity.version());
    }

    @Test
    void shouldRejectDuplicateOperationAtRootEntityBoundary() {
        DddEntity entity = DddEntity.open(new DddIdValue("ddd-001"));
        DddOperationIdValue operationId = new DddOperationIdValue("operation-001");
        DddOperationEntity operation = DddOperationEntity.pending(operationId, DddValue.positive(12), "DEFAULT");
        entity.confirm(operation, DddValue.positive(12), Instant.now());

        assertThrows(DomainValidationException.class,
                () -> entity.confirm(operation, DddValue.positive(12), Instant.now()));
    }
}
