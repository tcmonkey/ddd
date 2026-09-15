package com.ddd.domain.ddd.service;

import java.time.Instant;
import java.util.ConcurrentModificationException;

import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.param.DddDecisionParam;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.domain.ddd.repository.DddRuleRepository;

/**
 * DDD 写模式的领域决策服务模板。
 *
 * <p>该服务通过构造器获得领域仓储端口，不依赖 Spring API。它负责加载聚合、协同规则聚合与根实体，
 * 并在领域行为完成后保存完整聚合。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddWriteDomainService {
    private final DddRepository dddRepository;
    private final DddRuleRepository dddRuleRepository;

    public DddWriteDomainService(DddRepository dddRepository, DddRuleRepository dddRuleRepository) {
        this.dddRepository = dddRepository;
        this.dddRuleRepository = dddRuleRepository;
    }

    /**
     * 执行写入用例的领域协同，并保存经根实体确认后的完整聚合。
     *
     * @param param 仅包含输入聚合的领域参数
     * @return 写入或幂等重放的领域决策
     *
     * @author AIGenerator
     */
    public DddDecisionParam execute(DddWriteParam param) {
        DddEntity inputEntity = param.aggregate().entity();
        DddOperationEntity pendingOperation = inputEntity.requiredPendingOperation();
        DddAggregate aggregate = dddRepository.findById(inputEntity.id());
        DddEntity entity = aggregate.entity();
        DddOperationEntity existing = entity.findOperation(pendingOperation.operationId()).orElse(null);
        if (existing != null) {
            return DddDecisionParam.duplicate(existing.operationId(), existing.value(), entity.currentValue(),
                    "idempotent replay");
        }

        DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(pendingOperation.ruleCode());
        DddValue calculatedValue = rule.evaluate(new DddRuleParam(pendingOperation.ruleCode(),
                pendingOperation.baseValue()));
        DddOperationEntity confirmed = entity.confirm(pendingOperation, calculatedValue, Instant.now());
        if (!Boolean.TRUE.equals(dddRepository.save(aggregate))) {
            throw new ConcurrentModificationException("DDD 聚合根保存失败或发生版本冲突");
        }
        return DddDecisionParam.write(confirmed.operationId(), confirmed.value(), entity.currentValue(),
                rule.reason());
    }
}
