package com.ddd.application.ddd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.value.DddIdValue;
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
    private final DddRepository dddRepository;

    public DddReadApplication(DddRepository dddRepository) {
        this.dddRepository = dddRepository;
    }

    public DddReadResult query(String rawId) {
        DddIdValue id = new DddIdValue(rawId);
        return toView(dddRepository.findById(id));
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
