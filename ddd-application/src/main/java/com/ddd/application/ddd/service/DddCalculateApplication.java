package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.service.DddCalculateDomainService;

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
        return calculateDomainService.calculate(command.baseValue(), command.factor())
                .map(value -> new DddCalculateResult(value.value()));
    }
}
