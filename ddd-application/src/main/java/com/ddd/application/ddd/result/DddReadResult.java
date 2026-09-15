package com.ddd.application.ddd.result;

import java.time.Instant;
import java.util.List;

/**
 * DDD 域内读模式的应用层结果。
 *
 * <p>该视图独立于 client DTO，避免外部协议反向影响应用层。</p>
 *
 * @author AIGenerator
 */
public record DddReadResult(String id, int currentValue, List<EntityView> entities) {
    public record EntityView(String operationId, int value, String ruleCode, Instant occurredAt) {
    }
}
