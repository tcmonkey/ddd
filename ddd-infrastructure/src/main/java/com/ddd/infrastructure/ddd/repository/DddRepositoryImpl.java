package com.ddd.infrastructure.ddd.repository;

import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.infrastructure.DddBaseRepository;
import com.ddd.infrastructure.ddd.mysql.mapper.DddMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddPO;
import com.ddd.infrastructure.ddd.mysql.pojo.DddEntityPO;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 * DDD 聚合根的 MyBatis-Plus 仓储实现模板。
 *
 * <p>聚合根自身的基础 CRUD 由 {@link DddBaseRepository} 提供；实体查询使用 MyBatis-Plus
 * 条件构造器生成，不在 Mapper 或 XML 中编写自定义 SQL。</p>
 */
@Repository
public class DddRepositoryImpl
        extends DddBaseRepository<DddMapper, DddPO>
        implements DddRepository {
    private final DddEntityRepository entityRepository;

    public DddRepositoryImpl(DddMapper dddMapper,
                                                    DddEntityRepository entityRepository) {
        super(dddMapper);
        this.entityRepository = entityRepository;
    }

    @Override
    public DddAggregate findById(DddIdValue id) {
        DddPO aggregate = getById(id.value());
        if (aggregate == null) {
            return DddAggregate.open(id);
        }
        return DddAggregate.restore(
                new DddIdValue(aggregate.getId()),
                new DddValue(aggregate.getCurrentValue()),
                aggregate.getVersion(),
                entityRepository.findById(id.value())
                        .stream()
                        .map(this::toEntity)
                        .toList());
    }

    @Override
    public Boolean save(DddAggregate aggregate) {
        DddPO stored = getById(aggregate.id().value());
        boolean aggregateSaved;
        if (stored == null) {
            aggregateSaved = super.save(toDddPO(aggregate, aggregate.version()));
        } else {
            DddPO update = toDddPO(aggregate, aggregate.version() - 1);
            aggregateSaved = super.updateById(update);
        }
        return aggregateSaved && saveNewEntities(aggregate);
    }

    private Boolean saveNewEntities(DddAggregate aggregate) {
        Set<String> storedOperationIds = new HashSet<>(entityRepository.findById(aggregate.id().value())
                .stream()
                .map(DddEntityPO::getOperationId)
                .collect(java.util.stream.Collectors.toSet()));
        for (DddEntity item : aggregate.entities()) {
            if (!storedOperationIds.contains(item.operationId().value()) && !entityRepository.save(toEntityPO(aggregate, item))) {
                return false;
            }
        }
        return true;
    }

    private DddPO toDddPO(DddAggregate aggregate, long version) {
        DddPO po = new DddPO();
        po.setId(aggregate.id().value());
        po.setCurrentValue(aggregate.currentValue().value());
        po.setVersion(version);
        return po;
    }

    private DddEntityPO toEntityPO(DddAggregate aggregate, DddEntity item) {
        DddEntityPO po = new DddEntityPO();
        po.setOperationId(item.operationId().value());
        po.setId(aggregate.id().value());
        po.setBusinessValue(item.value().value());
        po.setRuleCode(item.ruleCode());
        po.setOccurredAt(item.occurredAt());
        return po;
    }

    private DddEntity toEntity(DddEntityPO po) {
        return new DddEntity(new DddOperationIdValue(po.getOperationId()), new DddValue(po.getBusinessValue()),
                po.getRuleCode(), po.getOccurredAt());
    }
}
