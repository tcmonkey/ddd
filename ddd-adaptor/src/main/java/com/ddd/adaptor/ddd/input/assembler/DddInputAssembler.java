package com.ddd.adaptor.ddd.input.assembler;

import java.util.List;

import org.springframework.stereotype.Component;

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
import com.ddd.client.ddd.request.DddCalculateRequest;
import com.ddd.client.ddd.request.DddRuleRequest;
import com.ddd.client.ddd.request.DddWriteRequest;
import com.ddd.client.ddd.response.DddCalculateResponse;
import com.ddd.client.ddd.response.DddExternalReadResponse;
import com.ddd.client.ddd.response.DddReadResponse;
import com.ddd.client.ddd.response.DddRuleResponse;
import com.ddd.client.ddd.response.DddWriteResponse;

/**
 * HTTP 协议 DTO 与应用层契约之间的防腐层。
 *
 * <p>所有转换集中在此处，避免 client 类型泄漏到 application 或 domain。</p>
 *
 * @author AIGenerator
 */
@Component
public final class DddInputAssembler {
    /**
     * 将 HTTP 写模式请求转换为应用层命令。
     *
     * @param request 写模式 HTTP 请求
     * @return 写模式应用命令
     *
     * @author AIGenerator
     */
    public DddWriteCommand toCommand(DddWriteRequest request) {
        // 1. 从 HTTP 请求读取写入所需的基础字段。
        String id = request.id();
        String operationId = request.operationId();
        String ruleCode = request.ruleCode();
        int baseValue = request.baseValue();

        // 2. 组装 application 层命令。
        DddWriteCommand command = new DddWriteCommand(id, operationId, ruleCode, baseValue);
        return command;
    }

    /**
     * 将 HTTP 纯计算请求转换为应用层命令。
     *
     * @param request 纯计算 HTTP 请求
     * @return 纯计算应用命令
     *
     * @author AIGenerator
     */
    public DddCalculateCommand toCommand(DddCalculateRequest request) {
        // 1. 从 HTTP 请求读取纯计算参数。
        int baseValue = request.baseValue();
        int factor = request.factor();

        // 2. 组装 application 层命令。
        DddCalculateCommand command = new DddCalculateCommand(baseValue, factor);
        return command;
    }

    /**
     * 将 HTTP 路径标识转换为域内读取应用命令。
     *
     * <p>即使只有一个标识字段，跨 Application 边界也必须传递命令对象。</p>
     *
     * @param id HTTP 路径中的聚合标识
     * @return 域内读取应用命令
     *
     * @author AIGenerator
     */
    public DddReadCommand toReadCommand(String id) {
        // 1. 读取 HTTP 路径中的聚合标识。
        String aggregateId = id;

        // 2. 组装域内读取应用命令。
        DddReadCommand command = new DddReadCommand(aggregateId);
        return command;
    }

    /**
     * 将 HTTP 路径标识转换为外部读取应用命令。
     *
     * <p>即使只有一个标识字段，跨 Application 边界也必须传递命令对象。</p>
     *
     * @param id HTTP 路径中的业务标识
     * @return 外部读取应用命令
     *
     * @author AIGenerator
     */
    public DddExternalReadCommand toExternalReadCommand(String id) {
        // 1. 读取 HTTP 路径中的业务标识。
        String businessId = id;

        // 2. 组装外部读取应用命令。
        DddExternalReadCommand command = new DddExternalReadCommand(businessId);
        return command;
    }

    /**
     * 将规则计算外部请求转换为应用层命令。
     *
     * @param request 规则计算外部请求
     * @return 应用层规则计算命令
     *
     * @author AIGenerator
     */
    public DddRuleCommand toCommand(DddRuleRequest request) {
        // 1. 从 HTTP 请求读取规则计算参数。
        String ruleCode = request.ruleCode();
        int baseValue = request.baseValue();

        // 2. 组装 application 层命令。
        DddRuleCommand command = new DddRuleCommand(ruleCode, baseValue);
        return command;
    }

    /**
     * 将写模式应用结果转换为 HTTP 响应。
     *
     * @param result 写模式应用结果
     * @return 写模式 HTTP 响应
     *
     * @author AIGenerator
     */
    public DddWriteResponse toResponse(DddWriteResult result) {
        // 1. 从应用层结果读取响应字段。
        String id = result.id();
        String operationId = result.operationId();
        int changedValue = result.changedValue();
        int currentValue = result.currentValue();
        boolean duplicate = result.duplicate();

        // 2. 组装 HTTP 响应。
        DddWriteResponse response = new DddWriteResponse(id, operationId, changedValue, currentValue, duplicate);
        return response;
    }

    /**
     * 将域内读取应用结果转换为 HTTP 响应。
     *
     * @param view 域内读取应用结果
     * @return 域内读取 HTTP 响应
     *
     * @author AIGenerator
     */
    public DddReadResponse toResponse(DddReadResult view) {
        // 1. 将应用层子实体视图转换为 HTTP 子项。
        List<DddReadResponse.EntityItem> entities = view.entities().stream()
                .map(item -> new DddReadResponse.EntityItem(item.operationId(), item.value(), item.ruleCode(),
                        item.occurredAt()))
                .toList();

        // 2. 组装 HTTP 响应。
        DddReadResponse response = new DddReadResponse(view.id(), view.currentValue(), entities);
        return response;
    }

    /**
     * 将纯计算应用结果转换为 HTTP 响应。
     *
     * @param result 纯计算应用结果
     * @return 纯计算 HTTP 响应
     *
     * @author AIGenerator
     */
    public DddCalculateResponse toResponse(DddCalculateResult result) {
        // 1. 读取应用层计算结果。
        int calculatedValue = result.calculatedValue();

        // 2. 组装 HTTP 响应。
        DddCalculateResponse response = new DddCalculateResponse(calculatedValue);
        return response;
    }

    /**
     * 将规则计算应用层结果转换为外部响应。
     *
     * @param result 规则计算应用层结果
     * @return 规则计算外部响应
     *
     * @author AIGenerator
     */
    public DddRuleResponse toResponse(DddRuleResult result) {
        // 1. 从应用层结果读取规则计算字段。
        String ruleCode = result.ruleCode();
        int factor = result.factor();
        int calculatedValue = result.calculatedValue();
        String reason = result.reason();

        // 2. 组装 HTTP 响应。
        DddRuleResponse response = new DddRuleResponse(ruleCode, factor, calculatedValue, reason);
        return response;
    }

    /**
     * 将外部数据查询结果转换为 HTTP 响应。
     *
     * @param view 外部数据查询结果
     * @return 外部数据 HTTP 响应
     *
     * @author AIGenerator
     */
    public DddExternalReadResponse toResponse(DddExternalResult view) {
        // 1. 从应用层结果读取外部查询字段。
        String id = view.id();
        String name = view.name();
        String category = view.category();

        // 2. 组装 HTTP 响应。
        DddExternalReadResponse response = new DddExternalReadResponse(id, name, category);
        return response;
    }
}
