package com.ddd.application.ddd.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ddd.application.exception.ApplicationErrorCode;
import com.ddd.application.ddd.command.DddReadCommand;
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
     * @param command 域内读取应用命令
     * @return 域内读取结果
     *
     * @author AIGenerator
     */
    public Result<DddReadResult> query(DddReadCommand command) {
        try {
            // 1. 从应用命令提取聚合标识并读取完整聚合。
            String aggregateId = command.id();
            DddAggregate aggregate = dddRepository.findById(DddAggregate.idOf(aggregateId));

            // 2. 将聚合转换为应用层只读结果。
            DddReadResult result = toView(aggregate);
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 域内查询失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 域内查询发生未预期异常", exception);
            return Result.failure(ApplicationErrorCode.APPLICATION_PROCESS_FAILED);
        }
    }

    private DddReadResult toView(DddAggregate aggregate) {
        // 1. 获取聚合根实体的当前状态。
        DddEntity entity = aggregate.entity();

        // 2. 将聚合内子实体转换为应用层视图。
        List<DddReadResult.EntityView> items = entity.operationEntities().stream()
                .map(this::toEntityView)
                .toList();

        // 3. 组装应用层读取结果。
        String id = entity.id().value();
        int currentValue = entity.currentValue().value();
        DddReadResult result = new DddReadResult(id, currentValue, items);
        return result;
    }

    private DddReadResult.EntityView toEntityView(DddOperationEntity item) {
        // 1. 从领域子实体读取展示所需字段。
        String operationId = item.operationId().value();
        int value = item.value().value();
        String ruleCode = item.ruleCode();

        // 2. 组装应用层子实体视图。
        DddReadResult.EntityView view = new DddReadResult.EntityView(operationId, value, ruleCode,
                item.occurredAt());
        return view;
    }
}
