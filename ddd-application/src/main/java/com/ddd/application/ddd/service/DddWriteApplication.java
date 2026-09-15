package com.ddd.application.ddd.service;

import java.time.Clock;
import java.util.ConcurrentModificationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.param.DddDecisionParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.domain.ddd.service.DddWriteDomainService;

/**
 * DDD 写模式的应用服务模板。
 *
 * <p>负责检索和保存聚合根，但不直接修改状态；状态变更由
 * {@link DddAggregate} 统一维护。</p>
 *
 * @author AIGenerator
 */
@Service
public class DddWriteApplication {
    @Autowired
    private DddRepository dddRepository;

    @Autowired
    private DddWriteDomainService dddWriteDomainService;

    @Autowired
    private Clock clock;

    @Transactional
    public DddWriteResult execute(DddWriteCommand command) {
        DddIdValue id = new DddIdValue(command.id());
        DddOperationIdValue operationId = new DddOperationIdValue(command.operationId());
        DddAggregate aggregate = dddRepository.findById(id);
        DddWriteParam context = new DddWriteParam(
                operationId, command.ruleCode(), DddValue.positive(command.baseValue()));
        DddDecisionParam decision = dddWriteDomainService.decide(aggregate, context);

        if (decision.duplicate()) {
            return new DddWriteResult(id.value(), operationId.value(), decision.value().value(),
                    aggregate.currentValue().value(), true);
        }

        aggregate.write(decision.operationId(), decision.value(), command.ruleCode(), clock.instant());
        if (!Boolean.TRUE.equals(dddRepository.save(aggregate))) {
            throw new ConcurrentModificationException("DDD 聚合根保存失败或发生版本冲突");
        }
        return new DddWriteResult(id.value(), operationId.value(), decision.value().value(),
                aggregate.currentValue().value(), false);
    }
}
