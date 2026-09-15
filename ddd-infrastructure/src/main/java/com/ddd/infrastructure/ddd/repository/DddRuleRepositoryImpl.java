package com.ddd.infrastructure.ddd.repository;

import org.springframework.stereotype.Repository;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.repository.DddRuleRepository;
import com.ddd.infrastructure.DddBaseRepository;
import com.ddd.infrastructure.ddd.mysql.mapper.DddRuleMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddRulePO;

/**
 * 规则聚合根的 MyBatis-Plus 仓储实现。
 *
 * <p>从 {@code ddd_rule} 表读取规则并恢复领域模型；
 * 表结构预置，运行环境不预置演示规则。</p>
 *
 * @author AIGenerator
 */
@Repository
public class DddRuleRepositoryImpl extends DddBaseRepository<DddRuleMapper, DddRulePO> implements DddRuleRepository {
    /**
     * 根据规则编码查询聚合根，不存在时明确拒绝计算。
     *
     * @param ruleCode 规则编码
     * @return 规则聚合根
     *
     * @author AIGenerator
     */
    @Override
    public DddRuleAggregate getRequiredByRuleCode(String ruleCode) {
        DddRulePO stored = getById(ruleCode);
        if (stored == null) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_NOT_FOUND);
        }
        return new DddRuleAggregate(stored.getRuleCode(), stored.getFactor(), stored.getReason());
    }
}
