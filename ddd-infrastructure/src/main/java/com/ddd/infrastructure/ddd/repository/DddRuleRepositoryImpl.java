package com.ddd.infrastructure.ddd.repository;

import org.springframework.stereotype.Repository;

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
     * 根据规则编码查询聚合根，不存在时返回空值，由领域决定用例失败。
     *
     * @param ruleCode 规则编码
     * @return 规则聚合根，不存在时为空
     *
     * @author AIGenerator
     */
    @Override
    public DddRuleAggregate findByRuleCode(String ruleCode) {
        // 1. 使用 MyBatis-Plus 基础 CRUD 查询规则持久化对象。
        DddRulePO stored = getById(ruleCode);
        if (stored == null) {
            return null;
        }

        // 2. 将持久化对象恢复为规则聚合。
        DddRuleAggregate aggregate = new DddRuleAggregate(stored.getRuleCode(), stored.getFactor(),
                stored.getReason());
        return aggregate;
    }
}
