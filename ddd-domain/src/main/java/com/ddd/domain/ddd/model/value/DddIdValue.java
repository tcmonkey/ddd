package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainValidationException;

/** DDD 聚合根标识值对象模板。 */
public record DddIdValue(String value) {
    public DddIdValue {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("id must not be blank");
        }
    }
}
