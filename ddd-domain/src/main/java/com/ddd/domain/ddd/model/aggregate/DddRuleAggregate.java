package com.ddd.domain.ddd.model.aggregate;

import com.ddd.domain.ddd.model.entity.DddRuleEntity;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 规则与计算模式的聚合容器，仅持有规则实体。
 *
 * <p>规则状态和计算行为内聚在 Entity，聚合只提供场景语义入口。</p>
 *
 * @author AIGenerator
 */
public final class DddRuleAggregate {
    /**
     * 本场景的完整规则实体。
     *
     * @author AIGenerator
     */
    private final DddRuleEntity entity;

    /**
     * 由规则快照创建聚合，状态校验委托规则实体。
     *
     * @param ruleCode 规则编码
     * @param factor 正数计算因子
     * @param reason 规则说明
     *
     * @author AIGenerator
     */
    public DddRuleAggregate(String ruleCode, int factor, String reason) {
        this.entity = new DddRuleEntity(ruleCode, factor, reason);
    }

    /**
     * 以聚合语义入口委托实体进行规则计算。
     *
     * @param param 规则计算参数
     * @return 计算后的领域值
     *
     * @author AIGenerator
     */
    public DddValue evaluate(DddRuleParam param) {
        return entity.evaluate(param);
    }

    /**
     * 获取聚合的规则编码。
     *
     * @return 规则实体的编码
     *
     * @author AIGenerator
     */
    public String ruleCode() {
        return entity.ruleCode();
    }

    /**
     * 获取规则计算因子。
     *
     * @return 规则实体的正数因子
     *
     * @author AIGenerator
     */
    public int factor() {
        return entity.factor();
    }

    /**
     * 获取规则说明。
     *
     * @return 规则实体的业务说明
     *
     * @author AIGenerator
     */
    public String reason() {
        return entity.reason();
    }
}
