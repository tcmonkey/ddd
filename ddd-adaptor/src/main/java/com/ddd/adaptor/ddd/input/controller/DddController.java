package com.ddd.adaptor.ddd.input.controller;

import com.ddd.adaptor.ddd.input.assembler.DddInputAssembler;
import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.command.DddExternalReadCommand;
import com.ddd.application.ddd.command.DddReadCommand;
import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.application.ddd.service.DddCalculateApplication;
import com.ddd.application.ddd.service.DddExternalReadApplication;
import com.ddd.application.ddd.service.DddReadApplication;
import com.ddd.application.ddd.service.DddRuleApplication;
import com.ddd.application.ddd.service.DddWriteApplication;
import com.ddd.client.ddd.request.DddCalculateRequest;
import com.ddd.client.ddd.request.DddRuleRequest;
import com.ddd.client.ddd.request.DddWriteRequest;
import com.ddd.client.ddd.response.DddCalculateResponse;
import com.ddd.client.ddd.response.DddExternalReadResponse;
import com.ddd.client.ddd.response.DddReadResponse;
import com.ddd.client.ddd.response.DddRuleResponse;
import com.ddd.client.ddd.response.DddWriteResponse;
import com.ddd.common.result.Result;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DDD 公共模板的 HTTP 输入适配器。
 *
 * <p>负责协议校验、请求组装、调用单个应用服务和响应转换，不承载领域业务规则。
 *
 * @author AIGenerator
 */
@RestController
@RequestMapping("/api/ddd")
public class DddController {

    private final DddWriteApplication writeApplication;
    private final DddReadApplication readApplication;
    private final DddCalculateApplication calculateApplication;
    private final DddRuleApplication ruleApplication;
    private final DddExternalReadApplication externalReadApplication;
    private final DddInputAssembler assembler;

    public DddController(
            DddWriteApplication writeApplication,
            DddReadApplication readApplication,
            DddCalculateApplication calculateApplication,
            DddRuleApplication ruleApplication,
            DddExternalReadApplication externalReadApplication,
            DddInputAssembler assembler) {
        this.writeApplication = writeApplication;
        this.readApplication = readApplication;
        this.calculateApplication = calculateApplication;
        this.ruleApplication = ruleApplication;
        this.externalReadApplication = externalReadApplication;
        this.assembler = assembler;
    }

    /**
     * 接收写模式请求，并将处理委托给写应用服务。
     *
     * @param dddWriteRequest 写模式请求
     * @return 写模式处理结果
     * @author AIGenerator
     */
    @PostMapping("/write")
    public Result<DddWriteResponse> write(@Valid @RequestBody DddWriteRequest dddWriteRequest) {
        try {
            // 1. 将 HTTP 请求转换为应用命令。
            DddWriteCommand dddWriteCommand = assembler.toCommand(dddWriteRequest);
            // 2. 调用写入应用服务。
            Result<DddWriteResult> applicationResult = writeApplication.write(dddWriteCommand);
            if (!applicationResult.success()) {
                return Result.failure(applicationResult.code(), applicationResult.message());
            }
            // 3. 将应用结果转换为 HTTP 响应。
            DddWriteResponse response = assembler.toResponse(applicationResult.data());
            return Result.success(response);
        } catch (Exception exception) {
            return com.ddd.adaptor.common.HttpResults.capture(exception);
        }
    }

    /**
     * 查询当前领域内的聚合数据。
     *
     * @param id 聚合根标识
     * @return 领域内查询结果
     * @author AIGenerator
     */
    @GetMapping("/{id}")
    public Result<DddReadResponse> query(@PathVariable("id") String id) {
        try {
            // 1. 将 HTTP 路径标识转换为应用命令。
            DddReadCommand dddReadCommand = assembler.toReadCommand(id);
            // 2. 调用域内读取应用服务。
            Result<DddReadResult> applicationResult = readApplication.query(dddReadCommand);
            if (!applicationResult.success()) {
                return Result.failure(applicationResult.code(), applicationResult.message());
            }
            // 3. 将应用结果转换为 HTTP 响应。
            DddReadResponse response = assembler.toResponse(applicationResult.data());
            return Result.success(response);
        } catch (Exception exception) {
            return com.ddd.adaptor.common.HttpResults.capture(exception);
        }
    }

    /**
     * 执行不依赖聚合和仓储的纯计算模式。
     *
     * @param dddCalculateRequest 纯计算请求
     * @return 纯计算结果
     * @author AIGenerator
     */
    @PostMapping("/calculate")
    public Result<DddCalculateResponse> calculate(
            @Valid @RequestBody DddCalculateRequest dddCalculateRequest) {
        try {
            // 1. 将 HTTP 请求转换为应用命令。
            DddCalculateCommand dddCalculateCommand = assembler.toCommand(dddCalculateRequest);
            // 2. 调用纯计算应用服务。
            Result<DddCalculateResult> applicationResult =
                    calculateApplication.calculate(dddCalculateCommand);
            if (!applicationResult.success()) {
                return Result.failure(applicationResult.code(), applicationResult.message());
            }
            // 3. 将应用结果转换为 HTTP 响应。
            DddCalculateResponse response = assembler.toResponse(applicationResult.data());
            return Result.success(response);
        } catch (Exception exception) {
            return com.ddd.adaptor.common.HttpResults.capture(exception);
        }
    }

    /**
     * 按领域规则执行计算模式。
     *
     * @param dddRuleRequest 规则计算请求
     * @return 规则计算结果
     * @author AIGenerator
     */
    @PostMapping("/rule")
    public Result<DddRuleResponse> calculateRule(
            @Valid @RequestBody DddRuleRequest dddRuleRequest) {
        try {
            // 1. 将 HTTP 请求转换为应用命令。
            DddRuleCommand dddRuleCommand = assembler.toCommand(dddRuleRequest);
            // 2. 调用规则计算应用服务。
            Result<DddRuleResult> applicationResult = ruleApplication.calculate(dddRuleCommand);
            if (!applicationResult.success()) {
                return Result.failure(applicationResult.code(), applicationResult.message());
            }
            // 3. 将应用结果转换为 HTTP 响应。
            DddRuleResponse response = assembler.toResponse(applicationResult.data());
            return Result.success(response);
        } catch (Exception exception) {
            return com.ddd.adaptor.common.HttpResults.capture(exception);
        }
    }

    /**
     * 通过外部读取应用服务查询第三方数据。
     *
     * @param id 业务对象标识
     * @return 外部数据查询结果
     * @author AIGenerator
     */
    @GetMapping("/{id}/external")
    public Result<DddExternalReadResponse> queryExternal(@PathVariable("id") String id) {
        try {
            // 1. 将 HTTP 路径标识转换为应用命令。
            DddExternalReadCommand dddExternalReadCommand = assembler.toExternalReadCommand(id);
            // 2. 调用外部读取应用服务。
            Result<DddExternalResult> applicationResult =
                    externalReadApplication.query(dddExternalReadCommand);
            if (!applicationResult.success()) {
                return Result.failure(applicationResult.code(), applicationResult.message());
            }
            // 3. 将应用结果转换为 HTTP 响应。
            DddExternalReadResponse response = assembler.toResponse(applicationResult.data());
            return Result.success(response);
        } catch (Exception exception) {
            return com.ddd.adaptor.common.HttpResults.capture(exception);
        }
    }
}
