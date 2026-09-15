package com.ddd.infrastructure.ddd.mysql.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 规则聚合根的持久化对象，对应 {@code ddd_rule} 表。
 *
 * <p>仅在基础设施层使用，不直接暴露给领域或接口层。</p>
 *
 * @author AIGenerator
 */
@TableName("ddd_rule")
public class DddRulePO {
    /**
     * 规则编码，对应主键 {@code rule_code}。
     *
     * @author AIGenerator
     */
    @TableId("rule_code")
    private String ruleCode;

    /**
     * 规则计算因子，对应 {@code factor}。
     *
     * @author AIGenerator
     */
    private Integer factor;

    /**
     * 规则说明，对应 {@code reason}。
     *
     * @author AIGenerator
     */
    private String reason;

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
