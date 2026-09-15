package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 写模式领域决策所需的不可变参数。
 *
 * @param operationId 一次写操作的幂等标识
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddWriteParam(
        DddOperationIdValue operationId,
        String ruleCode,
        DddValue baseValue) {
    public DddWriteParam {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("ruleCode must not be blank");
        }
    }
}
