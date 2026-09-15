package com.ddd.application.ddd.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ddd.application.exception.ApplicationErrorCode;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.repository.DddRepository;

/**
 * DDD 域内读模式的应用服务模板。
 *
 * <p>仅转换聚合根数据，不承载业务规则。</p>
 *
 * @author AIGenerator
 */
@Service
public final class DddReadApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DddReadApplication.class);

    private final DddRepository dddRepository;

    public DddReadApplication(DddRepository dddRepository) {
        this.dddRepository = dddRepository;
    }

    /**
     * 按聚合根标识查询域内数据，并将领域实体转换为应用层结果。
     *
     * @param rawId 聚合根原始标识
     * @return 域内读取结果
     *
     * @author AIGenerator
     */
    public Result<DddReadResult> query(String rawId) {
        try {
            return Result.success(toView(dddRepository.findById(DddAggregate.idOf(rawId))));
        } catch (DomainException exception) {
            LOG.warn("DDD 域内查询失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 域内查询发生未预期异常", exception);
            return Result.failure(ApplicationErrorCode.APPLICATION_PROCESS_FAILED);
        }
    }

    private DddReadResult toView(DddAggregate aggregate) {
        DddEntity entity = aggregate.entity();
        List<DddReadResult.EntityView> items = entity.operationEntities().stream()
                .map(this::toEntityView)
                .toList();
        return new DddReadResult(entity.id().value(), entity.currentValue().value(), items);
    }

    private DddReadResult.EntityView toEntityView(DddOperationEntity item) {
        return new DddReadResult.EntityView(item.operationId().value(), item.value().value(),
                item.ruleCode(), item.occurredAt());
    }
}
