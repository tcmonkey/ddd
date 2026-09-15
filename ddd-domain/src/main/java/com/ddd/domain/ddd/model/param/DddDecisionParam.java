package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 聚合根变更前执行幂等判断和规则计算所得的决策参数。
 *
 * @author AIGenerator
 */
public record DddDecisionParam(DddOperationIdValue operationId, DddValue value, String reason, boolean duplicate) {
    public static DddDecisionParam write(DddOperationIdValue operationId, DddValue value, String reason) {
        return new DddDecisionParam(operationId, value, reason, false);
    }

    public static DddDecisionParam duplicate(DddOperationIdValue operationId, DddValue value, String reason) {
        return new DddDecisionParam(operationId, value, reason, true);
    }
}
