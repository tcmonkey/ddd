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
import com.ddd.infrastructure.exception.InfrastructureErrorCode;
import com.ddd.infrastructure.exception.InfrastructureException;
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
        // 1. 使用主键查询主聚合的持久化快照。
        DddPO stored = getById(id.value());
        if (stored == null) {
            // 2. 不存在时返回可用于首次写入的空聚合。
            DddEntity entity = DddEntity.open(id);
            DddAggregate aggregate = DddAggregate.of(entity);
            return aggregate;
        }

        // 3. 已存在时恢复根实体和子操作快照，再封装为聚合。
        DddIdValue storedId = new DddIdValue(stored.getId());
        DddValue currentValue = new DddValue(stored.getCurrentValue());
        List<DddOperationEntity> operationEntities = readOperationEntities(stored.getEntitiesJson());
        DddEntity entity = DddEntity.restore(storedId, currentValue, stored.getVersion(), operationEntities);
        DddAggregate aggregate = DddAggregate.of(entity);
        return aggregate;
    }

    @Override
    public Boolean save(DddAggregate aggregate) {
        // 1. 查询现有快照，用于区分插入与带版本的更新。
        DddPO stored = getById(aggregate.entity().id().value());
        if (stored == null) {
            // 2. 首次写入使用当前实体版本创建主表记录。
            DddPO insert = toDddPO(aggregate, aggregate.entity().version());
            return super.save(insert);
        }

        // 3. 更新时传入旧版本，交由 MyBatis-Plus 乐观锁校验。
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
        // 1. 提取聚合根实体的持久化状态。
        DddEntity entity = aggregate.entity();

        // 2. 序列化子实体快照并组装单表持久化对象。
        String entitiesJson = writeOperationEntities(entity.operationEntities());
        DddPO po = new DddPO();
        po.setId(entity.id().value());
        po.setCurrentValue(entity.currentValue().value());
        po.setVersion(version);
        po.setEntitiesJson(entitiesJson);
        return po;
    }

    /**
     * 将根实体内子操作实体转换为主表可保存的 JSON 快照。
     *
     * @param operationEntities 聚合内部实体
     * @return 实体 JSON 快照
     *
     * @author AIGenerator
     */
    private String writeOperationEntities(List<DddOperationEntity> operationEntities) {
        try {
            // 1. 将聚合内子实体转换为可持久化的 JSON 快照。
            String entitiesJson = objectMapper.writeValueAsString(operationEntities);
            return entitiesJson;
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(InfrastructureErrorCode.INFRASTRUCTURE_SNAPSHOT_SERIALIZE_FAILED,
                    exception);
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
        // 1. 在反序列化前校验快照内容完整性。
        if (entitiesJson == null || entitiesJson.isBlank()) {
            throw new InfrastructureException(InfrastructureErrorCode.INFRASTRUCTURE_SNAPSHOT_INVALID);
        }
        try {
            // 2. 将 JSON 快照恢复为领域子实体集合。
            List<DddOperationEntity> entities = objectMapper.readValue(entitiesJson, OPERATION_ENTITY_TYPE);
            if (entities == null || entities.contains(null)) {
                throw new InfrastructureException(InfrastructureErrorCode.INFRASTRUCTURE_SNAPSHOT_INVALID);
            }
            return entities;
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(InfrastructureErrorCode.INFRASTRUCTURE_SNAPSHOT_INVALID, exception);
        }
    }
}
