package com.ddd.domain.ddd.repository;

import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;

/**
 * DDD 规则聚合根查询的领域仓储端口。
 *
 * @author AIGenerator
 */
public interface DddRuleRepository {
    /**
     * 根据规则编码查询规则聚合根。
     *
     * <p>不存在时返回空值，是否拒绝计算由领域服务决定，不构造默认规则。</p>
     *
     * @param ruleCode 规则编码
     * @return 已恢复的规则聚合根，不存在时为空
     *
     * @author AIGenerator
     */
    DddRuleAggregate findByRuleCode(String ruleCode);
}
