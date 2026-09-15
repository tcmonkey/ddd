package com.ddd.client.ddd.response;

import java.time.Instant;
import java.util.List;

/**
 * DDD 域内读模式的 HTTP 响应。
 *
 * @param id 业务对象标识
 * @param currentValue 聚合根当前数值
 * @param entities 与聚合根关联的实体明细
 *
 * @author AIGenerator
 */
public record DddReadResponse(
        String id,
        int currentValue,
        List<EntityItem> entities) {
    /**
     * 对外展示的单条实体明细。
     *
     * @param operationId 写操作幂等标识
     * @param value 本次实体记录的数值
     * @param ruleCode 产生本次数值的规则编码
     * @param occurredAt 操作发生时间
     *
     * @author AIGenerator
     */
    public record EntityItem(
            String operationId,
            int value,
            String ruleCode,
            Instant occurredAt) {
    }
}
