package com.ddd.domain.ddd.service;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.common.result.Result;
import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.domain.ddd.repository.DddRuleRepository;
import com.ddd.model.ddd.DddWriteDO;

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
    public Result<DddWriteDO> execute(DddWriteParam param) {
        try {
            // 1. 从输入聚合取得待处理操作，保持领域服务只与聚合协作。
            DddAggregate inputAggregate = param.aggregate();
            DddOperationEntity pendingOperation = inputAggregate.requiredPendingOperation();

            // 2. 加载已持久化聚合，用于执行幂等判断和状态变更。
            DddAggregate aggregate = dddRepository.findById(inputAggregate.id());
            Optional<DddOperationEntity> existingOperation = aggregate.findOperation(pendingOperation.operationId());
            DddOperationEntity existing = existingOperation.orElse(null);
            if (existing != null) {
                // 3. 已存在相同操作时返回幂等决策，不再重复写入。
                DddWriteDO result = new DddWriteDO(existing.operationId().value(), existing.value().value(),
                        aggregate.currentValue().value(), "idempotent replay", true);
                return Result.success(result);
            }

            // 4. 读取规则并完成待处理操作的确认。
            DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(pendingOperation.ruleCode());
            DddRuleParam ruleParam = new DddRuleParam(pendingOperation.ruleCode(),
                    pendingOperation.baseValue().value());
            DddValue calculatedValue = rule.evaluate(ruleParam);
            DddOperationEntity confirmed = aggregate.confirm(pendingOperation, calculatedValue, Instant.now());

            // 5. 保存完整聚合，并返回领域决策结果。
            Boolean saved = dddRepository.save(aggregate);
            if (!Boolean.TRUE.equals(saved)) {
                throw new DomainException(DomainErrorCode.DOMAIN_CONCURRENT_CONFLICT);
            }
            DddWriteDO result = new DddWriteDO(confirmed.operationId().value(), confirmed.value().value(),
                    aggregate.currentValue().value(), rule.reason(), false);
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 领域写入失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 领域写入发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
