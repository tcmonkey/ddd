package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * DDD 纯计算模式的不可变领域参数。
 *
 * <p>领域服务公开方法只接收参数对象，不接收裸基本类型。</p>
 *
 * @param baseValue 参与计算的基础数值
 * @param factor 参与计算的因子
 *
 * @author AIGenerator
 */
public record DddCalculateParam(int baseValue, int factor) {
    /**
     * 校验纯计算所需的基础参数。
     *
     * @throws DomainException 当基础数值或因子非正时抛出
     *
     * @author AIGenerator
     */
    public DddCalculateParam {
        if (baseValue <= 0 || factor <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
    }
}
