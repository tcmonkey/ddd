package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 写模式领域决策所需的不可变参数。
 *
 * @author AIGenerator
 */
public record DddWriteParam(DddOperationIdValue operationId, String ruleCode, DddValue baseValue) {
    public DddWriteParam {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("ruleCode must not be blank");
        }
    }
}
