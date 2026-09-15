package com.ddd.domain.ddd.model.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 聚合根实体模板。
 *
 * <p>根实体持有主状态和子操作实体，是聚合内唯一允许变更状态的位置。
 * 聚合容器只持有本实体，
 * 不再维护重复的 id、当前值或版本字段。</p>
 *
 * @author AIGenerator
 */
public final class DddEntity {
    /**
     * 根实体业务标识。
     *
     * @author AIGenerator
     */
    private final DddIdValue id;

    /**
     * 已确认子操作累计得到的当前值。
     *
     * @author AIGenerator
     */
    private DddValue currentValue;

    /**
     * 根实体版本，用于持久化乐观锁。
     *
     * @author AIGenerator
     */
    private long version;

    /**
     * 根实体内的子操作实体集合。
     *
     * @author AIGenerator
     */
    private final List<DddOperationEntity> operationEntities;

    private DddEntity(DddIdValue id, DddValue currentValue, long version,
                      List<DddOperationEntity> operationEntities) {
        this.id = id;
        this.currentValue = currentValue;
        this.version = version;
        this.operationEntities = new ArrayList<>(operationEntities);
    }

    /**
     * 创建不含历史操作的新根实体。
     *
     * @param id 根实体业务标识
     * @return 新根实体
     *
     * @author AIGenerator
     */
    public static DddEntity open(DddIdValue id) {
        return new DddEntity(id, new DddValue(0), 0, List.of());
    }

    /**
     * 创建仅携带待处理操作的有效输入实体。
     *
     * <p>待处理操作是显式状态，而不是字段缺失的无效实体；
     * 领域服务会在规则计算后将其确认。</p>
     *
     * @param id 根实体业务标识
     * @param operation 待处理子操作实体
     * @return 输入阶段根实体
     *
     * @author AIGenerator
     */
    public static DddEntity draft(DddIdValue id, DddOperationEntity operation) {
        return new DddEntity(id, new DddValue(0), 0, List.of(operation));
    }

    /**
     * 根据原始写入数据创建仅携带待处理操作的根实体。
     *
     * <p>值对象和子操作实体的创建收敛在实体内，避免 application 直接操作领域值对象。</p>
     *
     * @param rawId 原始根实体标识
     * @param rawOperationId 原始操作幂等标识
     * @param rawBaseValue 原始基础数值
     * @param ruleCode 规则编码
     * @return 输入阶段根实体
     *
     * @author AIGenerator
     */
    public static DddEntity draft(String rawId, String rawOperationId, int rawBaseValue, String ruleCode) {
        // 1. 将原始输入封装为领域值对象。
        DddIdValue id = new DddIdValue(rawId);
        DddOperationIdValue operationId = new DddOperationIdValue(rawOperationId);
        DddValue baseValue = DddValue.positive(rawBaseValue);

        // 2. 创建待处理操作并组装输入实体。
        DddOperationEntity operation = DddOperationEntity.pending(operationId, baseValue, ruleCode);
        DddEntity entity = draft(id, operation);
        return entity;
    }

    /**
     * 从持久化数据恢复根实体及其已确认子操作。
     *
     * @param id 根实体业务标识
     * @param currentValue 当前累计值
     * @param version 乐观锁版本
     * @param operationEntities 子操作实体集合
     * @return 恢复后的根实体
     *
     * @author AIGenerator
     */
    public static DddEntity restore(DddIdValue id, DddValue currentValue, long version,
                                    List<DddOperationEntity> operationEntities) {
        return new DddEntity(id, currentValue, version, operationEntities);
    }

    /**
     * 返回输入聚合中唯一的待处理子操作。
     *
     * @return 待处理子操作实体
     * @throws DomainException 当输入不恰好包含一个待处理操作时抛出
     *
     * @author AIGenerator
     */
    public DddOperationEntity requiredPendingOperation() {
        if (operationEntities.size() != 1 || !operationEntities.get(0).pending()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
        return operationEntities.get(0);
    }

    /**
     * 确认子操作并由根实体维护累计值、幂等边界与版本。
     *
     * @param operation 待确认的子操作实体
     * @param calculatedValue 规则计算后的领域值
     * @param occurredAt 操作确认时间
     * @return 已确认的子操作实体
     * @throws DomainException 当操作标识重复时抛出
     *
     * @author AIGenerator
     */
    public DddOperationEntity confirm(DddOperationEntity operation, DddValue calculatedValue,
                                      Instant occurredAt) {
        if (findOperation(operation.operationId()).isPresent()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
        DddOperationEntity confirmed = operation.confirm(calculatedValue, occurredAt);
        currentValue = currentValue.add(confirmed.value());
        operationEntities.add(confirmed);
        version++;
        return confirmed;
    }

    /**
     * 按操作标识查询聚合内的子操作实体。
     *
     * @param operationId 操作幂等标识
     * @return 已存在的子操作实体；不存在时为空
     *
     * @author AIGenerator
     */
    public Optional<DddOperationEntity> findOperation(DddOperationIdValue operationId) {
        return operationEntities.stream().filter(item -> item.operationId().equals(operationId)).findFirst();
    }

    public DddIdValue id() {
        return id;
    }

    public DddValue currentValue() {
        return currentValue;
    }

    public long version() {
        return version;
    }

    public List<DddOperationEntity> operationEntities() {
        return List.copyOf(operationEntities);
    }
}
