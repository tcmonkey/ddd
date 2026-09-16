package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
/**
 * 规则与计算模式所需的不可变领域参数。
 *
 * <p>该参数不包含写模式特有的写入标识，
 * 避免纯规则计算复用无业务意义的伪造数据。</p>
 *
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddRuleParam(String ruleCode, int baseValue) {
    /**
     * 校验规则计算的必要参数。
     *
     * @throws DomainException 当规则编码为空时抛出
     *
     * @author AIGenerator
     */
    public DddRuleParam {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
        if (baseValue <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_RULE_INVALID);
        }
    }
}
