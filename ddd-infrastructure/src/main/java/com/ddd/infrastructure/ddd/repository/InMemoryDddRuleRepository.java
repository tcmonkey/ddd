package com.ddd.infrastructure.ddd.repository;

import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.exception.DomainValidationException;
import com.ddd.domain.ddd.repository.DddRuleRepository;

import java.util.Map;

/**
 * DDD 规则聚合根的内存仓储模板。
 *
 * <p>生产代码应从配置或存储加载相同的领域对象。</p>
 */
public final class InMemoryDddRuleRepository implements DddRuleRepository {
    private final Map<String, DddRuleAggregate> rules = Map.of(
            "DEFAULT", new DddRuleAggregate("DEFAULT", 1, "默认规则"),
            "DOUBLE", new DddRuleAggregate("DOUBLE", 2, "双倍规则")
    );

    @Override
    public DddRuleAggregate getRequiredByRuleCode(String ruleCode) {
        DddRuleAggregate rule = rules.get(ruleCode);
        if (rule == null) {
            throw new DomainValidationException("不支持的 ruleCode: " + ruleCode);
        }
        return rule;
    }
}
