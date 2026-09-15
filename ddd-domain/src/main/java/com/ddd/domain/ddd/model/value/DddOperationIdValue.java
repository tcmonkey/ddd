package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * DDD 写操作的幂等标识值对象模板。
 *
 * @param value 操作幂等标识的原始值
 *
 * @author AIGenerator
 */
public record DddOperationIdValue(String value) {
    public DddOperationIdValue {
        if (value == null || value.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
    }
}
