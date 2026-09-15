package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * 带有显式算术边界校验的非负领域值对象模板。
 *
 * @param value 非负领域数值
 *
 * @author AIGenerator
 */
public record DddValue(int value) {
    public DddValue {
        if (value < 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
    }

    public static DddValue positive(int value) {
        if (value <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
        return new DddValue(value);
    }

    public DddValue add(DddValue other) {
        return new DddValue(Math.addExact(value, other.value));
    }

    public DddValue multiply(int factor) {
        if (factor <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
        return new DddValue(Math.multiplyExact(value, factor));
    }
}
