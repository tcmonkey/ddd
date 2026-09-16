package com.ddd.domain.ddd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.common.result.Result;
import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRuleRepository;
import com.ddd.model.ddd.DddRuleCalculateDO;

/**
 * 规则聚合查询和计算的领域服务。
 *
 * <p>规则读取、领域计算和错误结果转换均在本公开入口完成，
 * Application 只接收领域决策结果。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddRuleDomainService {
    private static final Logger LOG = LoggerFactory.getLogger(DddRuleDomainService.class);

    private final DddRuleRepository dddRuleRepository;

    public DddRuleDomainService(DddRuleRepository dddRuleRepository) {
        this.dddRuleRepository = dddRuleRepository;
    }

    /**
     * 按规则编码读取聚合并完成计算。
     *
     * @param param 规则计算领域参数
     * @return 规则计算领域决策
     *
     * @author AIGenerator
     */
    public Result<DddRuleCalculateDO> calculate(DddRuleParam param) {
        try {
            // 1. 加载规则聚合，规则不存在时由仓储明确拒绝。
            DddRuleAggregate rule = dddRuleRepository.getRequiredByRuleCode(param.ruleCode());

            // 2. 让规则聚合完成计算并生成领域决策。
            DddValue calculatedValue = rule.evaluate(param);
            DddRuleCalculateDO result = new DddRuleCalculateDO(rule.ruleCode(), rule.factor(), calculatedValue.value(),
                    rule.reason());
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 规则领域处理失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 规则领域处理发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
