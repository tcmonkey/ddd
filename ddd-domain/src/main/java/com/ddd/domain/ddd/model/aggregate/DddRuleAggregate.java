package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 规则与计算模式使用的规则聚合根模板。
 *
 * @param ruleCode 选择或记录规则的编码
 * @param factor 乘法计算因子，必须为正数
 * @param reason 规则或领域决策的说明
 *
 * @author AIGenerator
 */
public record DddRuleAggregate(
        String ruleCode,
        int factor,
        String reason) {
    public DddRuleAggregate {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
        if (factor <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
    }

    /**
     * 根据规则计算业务值，不依赖写模式的操作标识。
     *
     * @param param 规则计算领域参数
     * @return 计算后的值对象
     *
     * @author AIGenerator
     */
    public DddValue evaluate(DddRuleParam param) {
        // 1. 校验参数所属规则与当前聚合一致。
        if (!ruleCode.equals(param.ruleCode())) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }

        // 2. 在聚合内将原始数值封装为值对象并完成规则计算。
        DddValue baseValue = DddValue.positive(param.baseValue());
        DddValue calculatedValue = baseValue.multiply(factor);
        return calculatedValue;
    }
}
