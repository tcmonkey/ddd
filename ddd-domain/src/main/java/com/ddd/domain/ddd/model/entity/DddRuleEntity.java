package com.ddd.domain.ddd.model.entity;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 内聚规则状态、规则校验和计算行为的领域实体。
 *
 * @param ruleCode 选择或记录规则的编码
 * @param factor 乘法计算因子，必须为正数
 * @param reason 规则或领域决策的说明
 *
 * @author AIGenerator
 */
public record DddRuleEntity(String ruleCode, int factor, String reason) {
    /**
     * 创建并校验完整规则实体。
     *
     * @param ruleCode 规则编码
     * @param factor 正数计算因子
     * @param reason 规则说明
     *
     * @author AIGenerator
     */
    public DddRuleEntity {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
        if (factor <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
    }

    /**
     * 校验规则归属并完成规则计算。
     *
     * @param param 当前规则的计算输入
     * @return 校验后的计算值
     *
     * @author AIGenerator
     */
    public DddValue evaluate(DddRuleParam param) {
        // 1. 校验输入所属规则，避免跨规则计算。
        if (!ruleCode.equals(param.ruleCode())) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }

        // 2. 由实体封装数值并执行带不变量校验的计算。
        DddValue baseValue = DddValue.positive(param.baseValue());
        DddValue calculatedValue = baseValue.multiply(factor);
        return calculatedValue;
    }
}
