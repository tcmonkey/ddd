package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.exception.DomainValidationException;
import com.ddd.domain.ddd.model.value.DddValue;

/** DDD 规则与计算模式使用的规则聚合根模板。 */
public record DddRuleAggregate(String ruleCode, int factor, String reason) {
    public DddRuleAggregate {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new DomainValidationException("ruleCode must not be blank");
        }
        if (factor <= 0) {
            throw new DomainValidationException("factor must be positive");
        }
    }

    public DddValue evaluate(DddWriteParam context) {
        return evaluate(new DddRuleParam(context.ruleCode(), context.baseValue()));
    }

    /**
     * 根据规则计算业务值，不依赖写模式的操作标识。
     *
     * @param param 规则计算领域参数
     * @return 计算后的值对象
     */
    public DddValue evaluate(DddRuleParam param) {
        if (!ruleCode.equals(param.ruleCode())) {
            throw new DomainValidationException("rule ruleCode does not match context");
        }
        return param.baseValue().multiply(factor);
    }
}
