package com.ddd.client.ddd.response;

import java.time.Instant;
import java.util.List;

/** DDD 域内读模式的 HTTP 响应。 */
public record DddReadResponse(String id, int currentValue, List<EntityItem> entities) {
    public record EntityItem(String operationId, int value, String ruleCode, Instant occurredAt) {
    }
}
