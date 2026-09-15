package com.ddd.application.ddd.service;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.domain.ddd.service.DddCalculateDomainService;
import org.springframework.stereotype.Service;

/** DDD 纯计算模式的应用服务边界。 */
@Service
public final class DddCalculateApplication {
    private final DddCalculateDomainService calculateDomainService;

    public DddCalculateApplication(DddCalculateDomainService calculateDomainService) {
        this.calculateDomainService = calculateDomainService;
    }

    public DddCalculateResult execute(DddCalculateCommand command) {
        int calculatedValue = calculateDomainService
                .calculate(command.baseValue(), command.factor())
                .value();
        return new DddCalculateResult(calculatedValue);
    }
}
