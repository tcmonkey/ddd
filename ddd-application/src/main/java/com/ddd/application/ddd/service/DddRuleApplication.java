package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRuleRepository;

/**
 * DDD 规则与计算模式的应用服务模板。
 *
 * <p>该用例读取规则聚合根，并由聚合根完成规则匹配与业务计算；不修改数据聚合根状态。</p>
 *
 * @author AIGenerator
 */
@Service
public final class DddRuleApplication {
    private final DddRuleRepository dddRuleRepository;

    public DddRuleApplication(DddRuleRepository dddRuleRepository) {
        this.dddRuleRepository = dddRuleRepository;
    }

    /**
     * 按规则聚合根计算业务值。
     *
     * @param command 规则编码和基础值
     * @return 规则计算结果
     *
     * @author AIGenerator
     */
    public DddRuleResult execute(DddRuleCommand command) {
        DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(command.ruleCode());
        DddRuleParam param = new DddRuleParam(
                command.ruleCode(), DddValue.positive(command.baseValue()));
        DddValue calculatedValue = rule.evaluate(param);
        return new DddRuleResult(rule.ruleCode(), rule.factor(), calculatedValue.value(), rule.reason());
    }
}
