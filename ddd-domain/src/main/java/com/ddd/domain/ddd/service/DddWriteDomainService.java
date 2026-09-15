package com.ddd.domain.ddd.service;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.common.result.Result;
import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.result.DddWriteDecision;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.domain.ddd.repository.DddRuleRepository;

/**
 * DDD 写模式的领域决策服务模板。
 *
 * <p>该服务通过构造器获得领域仓储端口，不依赖 Spring API。
 * 它负责加载聚合、协同规则聚合与根实体，
 * 并在领域行为完成后保存完整聚合。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddWriteDomainService {
    private static final Logger LOG = LoggerFactory.getLogger(DddWriteDomainService.class);

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
     * @return 写入或幂等重放的领域决策操作包装
     *
     * @author AIGenerator
     */
    public Result<DddWriteDecision> execute(DddWriteParam param) {
        try {
            DddEntity inputEntity = param.aggregate().entity();
            DddOperationEntity pendingOperation = inputEntity.requiredPendingOperation();
            DddAggregate aggregate = dddRepository.findById(inputEntity.id());
            DddEntity entity = aggregate.entity();
            DddOperationEntity existing = entity.findOperation(pendingOperation.operationId()).orElse(null);
            if (existing != null) {
                return Result.success(DddWriteDecision.duplicate(existing.operationId(), existing.value(),
                        entity.currentValue(), "idempotent replay"));
            }

            DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(pendingOperation.ruleCode());
            DddValue calculatedValue = rule.evaluate(new DddRuleParam(pendingOperation.ruleCode(),
                    pendingOperation.baseValue()));
            DddOperationEntity confirmed = entity.confirm(pendingOperation, calculatedValue, Instant.now());
            if (!Boolean.TRUE.equals(dddRepository.save(aggregate))) {
                throw new DomainException(DomainErrorCode.DOMAIN_CONCURRENT_CONFLICT);
            }
            return Result.success(DddWriteDecision.written(confirmed.operationId(), confirmed.value(),
                    entity.currentValue(), rule.reason()));
        } catch (DomainException exception) {
            LOG.warn("DDD 领域写入失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 领域写入发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
