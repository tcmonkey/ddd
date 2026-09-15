package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.service.DddRuleDomainService;

/**
 * DDD 规则与计算模式的应用服务模板。
 *
 * <p>该用例读取规则聚合根，并由聚合根完成规则匹配与业务计算；
 * 不修改数据聚合根状态。</p>
 *
 * @author AIGenerator
 */
@Service
public final class DddRuleApplication {
    private final DddRuleDomainService dddRuleDomainService;

    public DddRuleApplication(DddRuleDomainService dddRuleDomainService) {
        this.dddRuleDomainService = dddRuleDomainService;
    }

    /**
     * 按规则聚合根计算业务值。
     *
     * @param command 规则编码和基础值
     * @return 规则计算结果
     *
     * @author AIGenerator
     */
    public Result<DddRuleResult> execute(DddRuleCommand command) {
        return dddRuleDomainService.execute(command.ruleCode(), command.baseValue())
                .map(decision -> new DddRuleResult(decision.ruleCode(), decision.factor(),
                        decision.calculatedValue().value(), decision.reason()));
    }
}
