package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.model.result.DddRuleDecision;
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
        // 1. 调用规则领域服务取得领域决策。
        String ruleCode = command.ruleCode();
        int baseValue = command.baseValue();
        Result<DddRuleDecision> domainResult = dddRuleDomainService.execute(ruleCode, baseValue);
        if (!domainResult.success()) {
            return Result.failure(domainResult.code(), domainResult.message());
        }

        // 2. 将领域决策转换为应用层结果。
        DddRuleDecision decision = domainResult.data();
        String decidedRuleCode = decision.ruleCode();
        int factor = decision.factor();
        int calculatedValue = decision.calculatedValue().value();
        String reason = decision.reason();
        DddRuleResult result = new DddRuleResult(decidedRuleCode, factor, calculatedValue, reason);
        return Result.success(result);
    }
}
