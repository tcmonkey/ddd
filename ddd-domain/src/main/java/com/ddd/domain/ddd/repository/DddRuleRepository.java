package com.ddd.domain.ddd.repository;

import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;

/**
 * DDD 规则聚合根查询的领域仓储端口。
 *
 * @author AIGenerator
 */
public interface DddRuleRepository {
    /**
     * 根据规则编码查询必需的规则聚合根。
     *
     * <p>规则不存在时明确拒绝计算，不返回空值或构造默认规则。</p>
     *
     * @param ruleCode 规则编码
     * @return 已恢复的规则聚合根
     * @throws DomainException 当规则不存在或规则数据不满足领域约束时抛出
     *
     * @author AIGenerator
     */
    DddRuleAggregate getRequiredByRuleCode(String ruleCode);
}
