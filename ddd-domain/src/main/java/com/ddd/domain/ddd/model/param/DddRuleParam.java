package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 规则与计算模式所需的不可变领域参数。
 *
 * <p>该参数不包含写模式特有的写入标识，避免纯规则计算复用无业务意义的伪造数据。</p>
 */
public record DddRuleParam(String ruleCode, DddValue baseValue) {
    /**
     * 校验规则计算的必要参数。
     *
     * @throws IllegalArgumentException 当规则编码为空时抛出
     */
    public DddRuleParam {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("ruleCode must not be blank");
        }
    }
}
