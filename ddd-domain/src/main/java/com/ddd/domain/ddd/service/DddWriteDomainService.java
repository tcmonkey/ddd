package com.ddd.domain.ddd.service;

import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.param.DddDecisionParam;
import com.ddd.domain.ddd.repository.DddRuleRepository;

/**
 * DDD 写模式的领域决策服务模板。
 *
 * <p>该服务负责跨对象决策，不直接修改聚合根；实际状态变化仍由聚合根完成。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddWriteDomainService {
    private final DddRuleRepository dddRuleRepository;

    public DddWriteDomainService(DddRuleRepository dddRuleRepository) {
        this.dddRuleRepository = dddRuleRepository;
    }

    public DddDecisionParam decide(DddAggregate aggregate, DddWriteParam context) {
        return aggregate.findEntity(context.operationId())
                .map(item -> duplicate(context, item))
                .orElseGet(() -> write(context));
    }

    private DddDecisionParam duplicate(DddWriteParam context, DddEntity item) {
        return DddDecisionParam.duplicate(context.operationId(), item.value(), "idempotent replay");
    }

    private DddDecisionParam write(DddWriteParam context) {
        DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(context.ruleCode());
        return DddDecisionParam.write(context.operationId(), rule.evaluate(context), rule.reason());
    }
}
