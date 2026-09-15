package com.ddd.application.ddd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.domain.ddd.service.DddCalculateDomainService;

/**
 * DDD 纯计算模式的应用服务边界。
 *
 * @author AIGenerator
 */
@Service
public final class DddCalculateApplication {
    @Autowired
    private DddCalculateDomainService calculateDomainService;

    public DddCalculateResult execute(DddCalculateCommand command) {
        int calculatedValue = calculateDomainService
                .calculate(command.baseValue(), command.factor())
                .value();
        return new DddCalculateResult(calculatedValue);
    }
}
