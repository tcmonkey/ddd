package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * DDD 聚合根标识值对象模板。
 *
 * @param value 聚合根标识的原始值
 *
 * @author AIGenerator
 */
public record DddIdValue(String value) {
    /**
     * 创建并初始化 DddIdValue，校验或装配其所需输入。
     *
     * @param value 需校验的领域数值
     *
     * @author AIGenerator
     */
    public DddIdValue {
        if (value == null || value.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_ID_INVALID);
        }
    }
}
