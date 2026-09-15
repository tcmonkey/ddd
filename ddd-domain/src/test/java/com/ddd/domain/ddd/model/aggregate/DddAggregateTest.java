package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.exception.DomainValidationException;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddValue;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DddAggregateTest {
    @Test
    void shouldChangeAggregateAndCreateEntityTogether() {
        DddAggregate aggregate = DddAggregate.open(new DddIdValue("ddd-001"));

        aggregate.write(new DddOperationIdValue("operation-001"), DddValue.positive(12), "DEFAULT", Instant.parse("2026-09-14T00:00:00Z"));

        assertEquals(12, aggregate.currentValue().value());
        assertEquals(1, aggregate.entities().size());
        assertEquals(1, aggregate.version());
    }

    @Test
    void shouldRejectDuplicateOperationAtAggregateBoundary() {
        DddAggregate aggregate = DddAggregate.open(new DddIdValue("ddd-001"));
        DddOperationIdValue operationId = new DddOperationIdValue("operation-001");
        aggregate.write(operationId, DddValue.positive(12), "DEFAULT", Instant.now());

        assertThrows(DomainValidationException.class,
                () -> aggregate.write(operationId, DddValue.positive(12), "DEFAULT", Instant.now()));
    }
}
