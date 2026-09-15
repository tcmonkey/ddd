package com.ddd.domain.ddd.service;

import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 纯计算模式的领域服务模板。
 *
 * <p>不检索聚合根或仓储，也不变更领域状态；所有计算逻辑都收敛在领域服务中。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddCalculateDomainService {
    /**
     * 根据输入值和计算因子得到结果。
     *
     * @param baseValue 输入值
     * @param factor 计算因子
     * @return 计算结果值对象
     *
     * @author AIGenerator
     */
    public DddValue calculate(int baseValue, int factor) {
        return DddValue.positive(baseValue).multiply(factor);
    }
}
