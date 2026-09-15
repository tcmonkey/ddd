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
        if (!ruleCode.equals(param.ruleCode())) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
        return param.baseValue().multiply(factor);
    }

    /**
     * 使用原始基础数值执行规则计算。
     *
     * <p>值对象和领域参数的封装留在规则聚合内，Application 只传递原始命令数据。</p>
     *
     * @param baseValue 原始基础数值
     * @return 计算后的领域值
     *
     * @author AIGenerator
     */
    public DddValue evaluate(int baseValue) {
        return evaluate(new DddRuleParam(ruleCode, DddValue.positive(baseValue)));
    }
}
