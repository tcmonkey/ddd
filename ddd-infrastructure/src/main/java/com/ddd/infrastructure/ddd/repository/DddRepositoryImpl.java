package com.ddd.infrastructure.ddd.repository;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRepository;
import com.ddd.infrastructure.DddBaseRepository;
import com.ddd.infrastructure.ddd.mysql.mapper.DddMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddPO;

/**
 * DDD 聚合根的 MyBatis-Plus 仓储实现模板。
 *
 * <p>聚合根的基础 CRUD 由 {@link DddBaseRepository} 提供；根实体内子操作快照与主状态同存于
 * {@code ddd_data} 表，不在 Mapper 或 XML 中编写自定义 SQL。</p>
 *
 * @author AIGenerator
 */
@Repository
public class DddRepositoryImpl extends DddBaseRepository<DddMapper, DddPO> implements DddRepository {
    /**
     * 领域实体快照的 JSON 类型，用于在基础设施层恢复聚合内部实体。
     *
     * @author AIGenerator
     */
    private static final TypeReference<List<DddOperationEntity>> OPERATION_ENTITY_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public DddRepositoryImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public DddAggregate findById(DddIdValue id) {
        DddPO stored = getById(id.value());
        if (stored == null) {
            return DddAggregate.of(DddEntity.open(id));
        }
        return DddAggregate.of(DddEntity.restore(
                new DddIdValue(stored.getId()),
                new DddValue(stored.getCurrentValue()),
                stored.getVersion(),
                readOperationEntities(stored.getEntitiesJson())));
    }

    @Override
    public Boolean save(DddAggregate aggregate) {
        DddPO stored = getById(aggregate.entity().id().value());
        if (stored == null) {
            return super.save(toDddPO(aggregate, aggregate.entity().version()));
        }
        DddPO update = toDddPO(aggregate, aggregate.entity().version() - 1);
        return super.updateById(update);
    }

    /**
     * 将根实体主状态与完整子操作实体快照组装为单表持久化对象。
     *
     * @param aggregate 待保存的聚合根
     * @param version 插入时使用当前版本，更新时使用预期的旧版本
     * @return 主表持久化对象
     *
     * @author AIGenerator
     */
    private DddPO toDddPO(DddAggregate aggregate, long version) {
        DddEntity entity = aggregate.entity();
        DddPO po = new DddPO();
        po.setId(entity.id().value());
        po.setCurrentValue(entity.currentValue().value());
        po.setVersion(version);
        po.setEntitiesJson(writeOperationEntities(entity.operationEntities()));
        return po;
    }

    /**
     * 将根实体内子操作实体转换为主表可保存的 JSON 快照。
     *
     * @param entities 聚合内部实体
     * @return 实体 JSON 快照
     *
     * @author AIGenerator
     */
    private String writeOperationEntities(List<DddOperationEntity> operationEntities) {
        try {
            return objectMapper.writeValueAsString(operationEntities);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("DDD 领域实体快照无法序列化", exception);
        }
    }

    /**
     * 从主表 JSON 快照恢复根实体内子操作实体，不静默忽略缺失或损坏的数据。
     *
     * @param entitiesJson 主表中的实体 JSON 快照
     * @return 聚合内部实体
     *
     * @author AIGenerator
     */
    private List<DddOperationEntity> readOperationEntities(String entitiesJson) {
        if (entitiesJson == null || entitiesJson.isBlank()) {
            throw new IllegalStateException("DDD 领域实体快照缺失");
        }
        try {
            List<DddOperationEntity> entities = objectMapper.readValue(entitiesJson, OPERATION_ENTITY_TYPE);
            if (entities == null || entities.contains(null)) {
                throw new IllegalStateException("DDD 领域实体快照格式无效");
            }
            return entities;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("DDD 领域实体快照无法反序列化", exception);
        }
    }
}
