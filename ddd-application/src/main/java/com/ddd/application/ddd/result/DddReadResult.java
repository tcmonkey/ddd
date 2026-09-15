package com.ddd.application.ddd.result;

import java.time.Instant;
import java.util.List;

/**
 * DDD 域内读模式的应用层结果。
 *
 * <p>该视图独立于 client DTO，避免外部协议反向影响应用层。</p>
 *
 * @param id 业务对象标识
 * @param currentValue 聚合根当前数值
 * @param entities 与聚合根关联的实体明细
 *
 * @author AIGenerator
 */
public record DddReadResult(
        String id,
        int currentValue,
        List<EntityView> entities) {
    /**
     * 应用层使用的单条实体明细，独立于对外响应协议。
     *
     * @param operationId 写操作幂等标识
     * @param value 本次实体记录的数值
     * @param ruleCode 产生本次数值的规则编码
     * @param occurredAt 操作发生时间
     *
     * @author AIGenerator
     */
    public record EntityView(
            String operationId,
            int value,
            String ruleCode,
            Instant occurredAt) {
    }
}
