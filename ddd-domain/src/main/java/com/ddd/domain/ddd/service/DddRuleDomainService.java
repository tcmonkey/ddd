package com.ddd.domain.ddd.service;

import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.model.aggregate.DddRuleAggregate;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.domain.ddd.repository.DddRuleRepository;
import com.ddd.model.ddd.DddRuleCalculateDO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当前ddd示例业务内按规则完成计算的领域服务。
 *
 * <p>规则读取、领域计算和错误结果转换均在本公开入口完成， Application 只接收领域决策结果。
 *
 * @author AIGenerator
 */
@DomainService
public final class DddRuleDomainService {
    /**
     * 当前类的日志记录器。
     *
     * @author AIGenerator
     */
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
     * @author AIGenerator
     */
    public Result<DddRuleCalculateDO> calculate(DddRuleParam param) {
        try {
            // 1. 加载规则聚合，规则不存在时由本领域拒绝。
            DddRuleAggregate rule = dddRuleRepository.findByRuleCode(param.ruleCode());
            if (rule == null) {
                throw new DomainException(DomainErrorCode.DOMAIN_RULE_NOT_FOUND);
            }

            // 2. 让规则聚合完成计算并生成领域决策。
            DddValue calculatedValue = rule.evaluate(param);
            DddRuleCalculateDO result =
                    new DddRuleCalculateDO(
                            rule.ruleCode(), rule.factor(), calculatedValue.value(), rule.reason());
            return Result.success(result);
        } catch (com.ddd.common.error.BaseException exception) {
            LOG.warn("DDD 规则领域处理失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 规则领域处理发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
