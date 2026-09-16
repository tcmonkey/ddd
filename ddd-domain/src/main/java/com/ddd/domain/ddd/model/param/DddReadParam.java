package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * DDD 域内读取的领域参数。
 *
 * <p>领域仓储的公开查询契约使用参数对象，避免跨边界传递裸业务标识。</p>
 *
 * @param id 待查询的聚合根标识
 *
 * @author AIGenerator
 */
public record DddReadParam(String id) {
    /**
     * 校验域内读取所需的聚合标识。
     *
     * @throws DomainException 当聚合标识为空时抛出
     *
     * @author AIGenerator
     */
    public DddReadParam {
        if (id == null || id.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
    }
}
