package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainValidationException;

/** 带有显式算术边界校验的非负领域值对象模板。 */
public record DddValue(int value) {
    public DddValue {
        if (value < 0) {
            throw new DomainValidationException("value must not be negative");
        }
    }

    public static DddValue positive(int value) {
        if (value <= 0) {
            throw new DomainValidationException("value must be positive");
        }
        return new DddValue(value);
    }

    public DddValue add(DddValue other) {
        return new DddValue(Math.addExact(value, other.value));
    }

    public DddValue multiply(int factor) {
        if (factor <= 0) {
            throw new DomainValidationException("factor must be positive");
        }
        return new DddValue(Math.multiplyExact(value, factor));
    }
}
