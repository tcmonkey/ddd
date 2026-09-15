package com.ddd.domain.ddd.repository;

import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;

/**
 * DDD 规则聚合根查询的领域仓储端口。
 *
 * @author AIGenerator
 */
public interface DddRuleRepository {
    DddRuleAggregate getRequiredByRuleCode(String ruleCode);
}
