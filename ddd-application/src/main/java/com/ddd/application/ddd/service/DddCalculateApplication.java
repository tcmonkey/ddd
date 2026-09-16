package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.model.param.DddCalculateParam;
import com.ddd.domain.ddd.service.DddCalculateDomainService;
import com.ddd.model.ddd.DddCalculateDO;

/**
 * DDD 纯计算模式的应用服务边界。
 *
 * @author AIGenerator
 */
@Service
public final class DddCalculateApplication {
    private final DddCalculateDomainService calculateDomainService;

    public DddCalculateApplication(DddCalculateDomainService calculateDomainService) {
        this.calculateDomainService = calculateDomainService;
    }

    /**
     * 调用无状态领域服务执行数值计算，并转换为应用层结果。
     *
     * @param command 纯计算应用命令
     * @return 纯计算应用结果
     *
     * @author AIGenerator
     */
    public Result<DddCalculateResult> execute(DddCalculateCommand command) {
        // 1. 将应用命令转换为领域参数并调用无状态领域服务。
        DddCalculateParam param = new DddCalculateParam(command.baseValue(), command.factor());
        Result<DddCalculateDO> domainResult = calculateDomainService.calculate(param);
        if (!domainResult.success()) {
            return Result.failure(domainResult.code(), domainResult.message());
        }

        // 2. 将领域内部数据对象转换为应用层结果。
        DddCalculateDO dataObject = domainResult.data();
        DddCalculateResult result = new DddCalculateResult(dataObject.calculatedValue());
        return Result.success(result);
    }
}
