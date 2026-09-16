package com.ddd.domain.ddd.model.aggregate;

import java.time.Instant;
import java.util.Optional;

import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 写模式的聚合容器模板。
 *
 * <p>聚合仅持有根实体，不保存任何独立业务状态。
 * 根实体及其子操作实体共同表达一个事务一致性边界，
 * 所有状态变化都委托给根实体的方法完成。</p>
 *
 * @author AIGenerator
 */
public final class DddAggregate {
    /**
     * 聚合根实体，持有本场景的主状态与子操作实体。
     *
     * @author AIGenerator
     */
    private final DddEntity entity;

    private DddAggregate(DddEntity entity) {
        this.entity = entity;
    }

    /**
     * 以根实体创建聚合。
     *
     * @param entity 聚合根实体
     * @return 领域聚合
     *
     * @author AIGenerator
     */
    public static DddAggregate of(DddEntity entity) {
        return new DddAggregate(entity);
    }

    /**
     * 根据原始写入数据创建仅含待处理操作的输入聚合。
     *
     * <p>Application 只传递命令中的原始数据，不创建或操作值对象。
     * 根实体负责将原始数据封装为值对象和
     * 子操作实体，从而确保模型构造规则留在聚合边界内。</p>
     *
     * @param rawId 原始根实体标识
     * @param rawOperationId 原始操作幂等标识
     * @param rawBaseValue 原始基础数值
     * @param ruleCode 规则编码
     * @return 待领域服务处理的输入聚合
     *
     * @author AIGenerator
     */
    public static DddAggregate draft(String rawId, String rawOperationId, int rawBaseValue, String ruleCode) {
        // 1. 由根实体封装原始输入为领域模型。
        DddEntity entity = DddEntity.draft(rawId, rawOperationId, rawBaseValue, ruleCode);

        // 2. 使用根实体创建输入聚合。
        DddAggregate aggregate = of(entity);
        return aggregate;
    }

    /**
     * 获取输入聚合中唯一的待处理操作。
     *
     * <p>领域服务通过聚合访问根实体行为，避免直接面向实体编排过程。</p>
     *
     * @return 待处理的操作实体
     *
     * @author AIGenerator
     */
    public DddOperationEntity requiredPendingOperation() {
        return entity.requiredPendingOperation();
    }

    /**
     * 获取根实体标识。
     *
     * @return 根实体标识
     *
     * @author AIGenerator
     */
    public DddIdValue id() {
        return entity.id();
    }

    /**
     * 按幂等标识查找聚合内已经确认的操作。
     *
     * @param operationId 操作幂等标识
     * @return 已存在的操作；不存在时为空
     *
     * @author AIGenerator
     */
    public Optional<DddOperationEntity> findOperation(DddOperationIdValue operationId) {
        return entity.findOperation(operationId);
    }

    /**
     * 确认待处理操作并由根实体完成状态变更。
     *
     * @param operation 待确认的操作
     * @param calculatedValue 规则计算后的数值
     * @param occurredAt 操作发生时间
     * @return 已确认的操作实体
     *
     * @author AIGenerator
     */
    public DddOperationEntity confirm(DddOperationEntity operation, DddValue calculatedValue,
                                      Instant occurredAt) {
        return entity.confirm(operation, calculatedValue, occurredAt);
    }

    /**
     * 获取根实体当前累计值。
     *
     * @return 当前累计值
     *
     * @author AIGenerator
     */
    public DddValue currentValue() {
        return entity.currentValue();
    }

    /**
     * 获取聚合根实体。
     *
     * @return 聚合根实体
     *
     * @author AIGenerator
     */
    public DddEntity entity() {
        return entity;
    }
}
