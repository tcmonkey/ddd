package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.assembler.DddApplicationAssembler;
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
    /**
     * 纯计算用例的领域服务。
     *
     * @author AIGenerator
     */
    private final DddCalculateDomainService calculateDomainService;
    /**
     * 本层参数组装与结果转换组件。
     *
     * @author AIGenerator
     */
    private final DddApplicationAssembler assembler;

    /**
     * 创建并初始化 DddCalculateApplication，校验或装配其所需输入。
     *
     * @param calculateDomainService 纯计算用例的领域服务
     * @param assembler 本层参数组装与结果转换组件
     *
     * @author AIGenerator
     */
    public DddCalculateApplication(DddCalculateDomainService calculateDomainService,
                                   DddApplicationAssembler assembler) {
        this.calculateDomainService = calculateDomainService;
        this.assembler = assembler;
    }

    /**
     * 调用无状态领域服务执行数值计算，并转换为应用层结果。
     *
     * @param dddCalculateCommand 纯计算应用命令
     * @return 纯计算应用结果
     *
     * @author AIGenerator
     */
    public Result<DddCalculateResult> calculate(DddCalculateCommand dddCalculateCommand) {
        // 1. 将应用命令组装为纯计算领域参数。
        DddCalculateParam param = assembler.toDomainParam(dddCalculateCommand);

        // 2. 调用无状态领域服务。
        Result<DddCalculateDO> domainResult = calculateDomainService.calculate(param);
        if (!domainResult.success()) {
            return Result.failure(domainResult.code(), domainResult.message());
        }

        // 3. 将领域内部数据对象转换为应用层结果。
        DddCalculateDO dataObject = domainResult.data();
        DddCalculateResult result = assembler.toResult(dataObject);
        return Result.success(result);
    }
}
