package com.ddd.domain.ddd.model.entity;

import java.time.Instant;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 聚合根实体持有的单次操作子实体。
 *
 * <p>待处理状态只包含原始输入；确认后必须同时具有计算值和发生时间。
 * 该状态机使 application 可以先构造输入实体，再由领域服务与根实体完成初始化，
 * 而不会产生语义不完整的对象。</p>
 *
 * @param operationId 一次写操作的幂等标识
 * @param baseValue 参与规则计算的原始数值
 * @param ruleCode 规则编码
 * @param value 确认后的领域数值；待处理时为空
 * @param occurredAt 操作确认时间；待处理时为空
 *
 * @author AIGenerator
 */
public record DddOperationEntity(
        DddOperationIdValue operationId,
        DddValue baseValue,
        String ruleCode,
        DddValue value,
        Instant occurredAt) {
    public DddOperationEntity {
        if (operationId == null || baseValue == null || ruleCode == null || ruleCode.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
        if ((value == null) != (occurredAt == null)) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
    }

    /**
     * 创建待规则计算的子操作实体。
     *
     * @param operationId 操作幂等标识
     * @param baseValue 原始数值
     * @param ruleCode 规则编码
     * @return 待处理子操作实体
     *
     * @author AIGenerator
     */
    public static DddOperationEntity pending(DddOperationIdValue operationId, DddValue baseValue,
                                             String ruleCode) {
        return new DddOperationEntity(operationId, baseValue, ruleCode, null, null);
    }

    /**
     * 将待处理操作确认成可持久化的领域实体。
     *
     * @param calculatedValue 规则计算后的领域值
     * @param occurredAt 操作确认时间
     * @return 已确认子操作实体
     *
     * @author AIGenerator
     */
    public DddOperationEntity confirm(DddValue calculatedValue, Instant occurredAt) {
        if (!pending()) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
        return new DddOperationEntity(operationId, baseValue, ruleCode, calculatedValue, occurredAt);
    }

    /**
     * 判断操作是否尚未完成规则计算和确认。
     *
     * @return 待处理返回 {@code true}
     *
     * @author AIGenerator
     */
    public boolean pending() {
        return value == null;
    }
}
