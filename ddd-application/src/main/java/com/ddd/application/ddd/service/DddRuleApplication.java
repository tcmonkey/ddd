package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.assembler.DddApplicationAssembler;
import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.service.DddRuleDomainService;
import com.ddd.model.ddd.DddRuleCalculateDO;

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
    private final DddApplicationAssembler assembler;

    public DddRuleApplication(DddRuleDomainService dddRuleDomainService, DddApplicationAssembler assembler) {
        this.dddRuleDomainService = dddRuleDomainService;
        this.assembler = assembler;
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
        // 1. 将应用命令组装为规则计算领域参数。
        DddRuleParam param = assembler.toDomainParam(command);

        // 2. 调用规则领域服务取得领域内部数据对象。
        Result<DddRuleCalculateDO> domainResult = dddRuleDomainService.execute(param);
        if (!domainResult.success()) {
            return Result.failure(domainResult.code(), domainResult.message());
        }

        // 3. 将领域内部数据对象转换为应用层结果。
        DddRuleCalculateDO dataObject = domainResult.data();
        DddRuleResult result = assembler.toResult(dataObject);
        return Result.success(result);
    }
}
