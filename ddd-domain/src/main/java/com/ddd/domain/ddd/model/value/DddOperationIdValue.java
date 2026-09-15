package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainValidationException;

/** DDD 写操作的幂等标识值对象模板。 */
public record DddOperationIdValue(String value) {
    public DddOperationIdValue {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("operationId must not be blank");
        }
    }
}
