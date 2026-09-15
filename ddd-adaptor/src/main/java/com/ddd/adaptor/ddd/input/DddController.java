package com.ddd.adaptor.ddd.input;

import com.ddd.adaptor.ddd.input.assembler.DddInputAssembler;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.application.ddd.service.DddCalculateApplication;
import com.ddd.application.ddd.service.DddRuleApplication;
import com.ddd.application.ddd.service.DddWriteApplication;
import com.ddd.application.ddd.service.DddExternalReadApplication;
import com.ddd.application.ddd.service.DddReadApplication;
import com.ddd.client.common.Result;
import com.ddd.client.ddd.request.DddCalculateRequest;
import com.ddd.client.ddd.request.DddRuleRequest;
import com.ddd.client.ddd.request.DddWriteRequest;
import com.ddd.client.ddd.response.DddWriteResponse;
import com.ddd.client.ddd.response.DddExternalReadResponse;
import com.ddd.client.ddd.response.DddReadResponse;
import com.ddd.client.ddd.response.DddCalculateResponse;
import com.ddd.client.ddd.response.DddRuleResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DDD 公共模板的输入适配器。
 *
 * <p>负责协议校验、请求组装、调用单个应用服务和响应转换，不承载领域业务规则。</p>
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

    public DddController(DddWriteApplication writeApplication,
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

    @PostMapping("/write")
    public Result<DddWriteResponse> write(@Valid @RequestBody DddWriteRequest request) {
        DddWriteResult result = writeApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    @GetMapping("/{id}")
    public Result<DddReadResponse> query(@PathVariable("id") String id) {
        DddReadResult view = readApplication.query(id);
        return Result.success(assembler.toResponse(view));
    }

    @PostMapping("/calculate")
    public Result<DddCalculateResponse> calculate(@Valid @RequestBody DddCalculateRequest request) {
        DddCalculateResult result = calculateApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    /**
     * 执行规则与计算模式示例。
     *
     * @param request 规则计算请求
     * @return 规则计算响应
     *
     * @author AIGenerator
     */
    @PostMapping("/rule")
    public Result<DddRuleResponse> calculateRule(
            @Valid @RequestBody DddRuleRequest request) {
        DddRuleResult result = ruleApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    /**
     * 执行查询外部系统的读模式示例。
     *
     * @param id 业务对象标识
     * @return DDD 外部数据响应
     *
     * @author AIGenerator
     */
    @GetMapping("/{id}/external")
    public Result<DddExternalReadResponse> queryExternal(@PathVariable("id") String id) {
        DddExternalResult view = externalReadApplication.query(id);
        return Result.success(assembler.toResponse(view));
    }
}
