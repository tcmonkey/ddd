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

    /**
     * 获取规则编码。
     *
     * @return 对应字段的当前值
     *
     * @author AIGenerator
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * 设置规则编码。
     *
     * @param ruleCode 规则编码
     *
     * @author AIGenerator
     */
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    /**
     * 获取正数规则计算因子。
     *
     * @return 对应字段的当前值
     *
     * @author AIGenerator
     */
    public Integer getFactor() {
        return factor;
    }

    /**
     * 设置正数规则计算因子。
     *
     * @param factor 正数规则计算因子
     *
     * @author AIGenerator
     */
    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    /**
     * 获取规则或领域决策说明。
     *
     * @return 对应字段的当前值
     *
     * @author AIGenerator
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置规则或领域决策说明。
     *
     * @param reason 规则或领域决策说明
     *
     * @author AIGenerator
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
