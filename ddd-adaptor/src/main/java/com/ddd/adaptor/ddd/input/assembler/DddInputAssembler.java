package com.ddd.adaptor.ddd.input.assembler;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.client.ddd.request.DddCalculateRequest;
import com.ddd.client.ddd.request.DddRuleRequest;
import com.ddd.client.ddd.request.DddWriteRequest;
import com.ddd.client.ddd.response.DddWriteResponse;
import com.ddd.client.ddd.response.DddExternalReadResponse;
import com.ddd.client.ddd.response.DddReadResponse;
import com.ddd.client.ddd.response.DddCalculateResponse;
import com.ddd.client.ddd.response.DddRuleResponse;

/**
 * HTTP 协议 DTO 与应用层契约之间的防腐层。
 *
 * <p>所有转换集中在此处，避免 client 类型泄漏到 application 或 domain。</p>
 */
public final class DddInputAssembler {
    public DddWriteCommand toCommand(DddWriteRequest request) {
        return new DddWriteCommand(request.id(), request.operationId(), request.ruleCode(),
                request.baseValue());
    }

    public DddCalculateCommand toCommand(DddCalculateRequest request) {
        return new DddCalculateCommand(request.baseValue(), request.factor());
    }

    /**
     * 将规则计算外部请求转换为应用层命令。
     *
     * @param request 规则计算外部请求
     * @return 应用层规则计算命令
     */
    public DddRuleCommand toCommand(DddRuleRequest request) {
        return new DddRuleCommand(request.ruleCode(), request.baseValue());
    }

    public DddWriteResponse toResponse(DddWriteResult result) {
        return new DddWriteResponse(result.id(), result.operationId(), result.changedValue(),
                result.currentValue(), result.duplicate());
    }

    public DddReadResponse toResponse(DddReadResult view) {
        return new DddReadResponse(view.id(), view.currentValue(), view.entities().stream()
                .map(item -> new DddReadResponse.EntityItem(item.operationId(), item.value(), item.ruleCode(),
                        item.occurredAt()))
                .toList());
    }

    public DddCalculateResponse toResponse(DddCalculateResult result) {
        return new DddCalculateResponse(result.calculatedValue());
    }

    /**
     * 将规则计算应用层结果转换为外部响应。
     *
     * @param result 规则计算应用层结果
     * @return 规则计算外部响应
     */
    public DddRuleResponse toResponse(DddRuleResult result) {
        return new DddRuleResponse(result.ruleCode(), result.factor(), result.calculatedValue(),
                result.reason());
    }

    /**
     * 将外部数据查询结果转换为 HTTP 响应。
     *
     * @param view 外部数据查询结果
     * @return 外部数据 HTTP 响应
     */
    public DddExternalReadResponse toResponse(DddExternalResult view) {
        return new DddExternalReadResponse(view.id(), view.name(), view.category());
    }
}
