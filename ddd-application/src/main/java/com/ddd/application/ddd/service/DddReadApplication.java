package com.ddd.application.ddd.service;

import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.repository.DddRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DDD 域内读模式的应用服务模板。
 *
 * <p>仅转换聚合根数据，不承载业务规则。</p>
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
        List<DddReadResult.EntityView> items = aggregate.entities().stream()
                .map(this::toEntityView)
                .toList();
        return new DddReadResult(aggregate.id().value(), aggregate.currentValue().value(), items);
    }

    private DddReadResult.EntityView toEntityView(DddEntity item) {
        return new DddReadResult.EntityView(item.operationId().value(), item.value().value(),
                item.ruleCode(), item.occurredAt());
    }
}
