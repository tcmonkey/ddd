package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 聚合根变更前执行幂等判断和规则计算所得的决策参数。
 *
 * @param operationId 一次写操作的幂等标识
 * @param value 领域决策得到的本次数值
 * @param reason 规则或领域决策的说明
 * @param duplicate 是否为幂等重放
 *
 * @author AIGenerator
 */
public record DddDecisionParam(
        DddOperationIdValue operationId,
        DddValue value,
        String reason,
        boolean duplicate) {
    public static DddDecisionParam write(DddOperationIdValue operationId, DddValue value, String reason) {
        return new DddDecisionParam(operationId, value, reason, false);
    }

    public static DddDecisionParam duplicate(DddOperationIdValue operationId, DddValue value, String reason) {
        return new DddDecisionParam(operationId, value, reason, true);
    }
}
