package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.model.entity.DddEntity;

/**
 * DDD 写模式的聚合容器模板。
 *
 * <p>聚合仅持有根实体，不保存任何独立业务状态。根实体及其子操作实体共同表达一个事务一致性边界，
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
     * <p>Application 只传递命令中的原始数据，不创建或操作值对象。根实体负责将原始数据封装为值对象和
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
        return of(DddEntity.draft(rawId, rawOperationId, rawBaseValue, ruleCode));
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
