package com.ddd.adaptor.ddd.input;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddd.adaptor.ddd.input.assembler.DddInputAssembler;
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
import com.ddd.client.common.Result;
import com.ddd.client.ddd.request.DddCalculateRequest;
import com.ddd.client.ddd.request.DddRuleRequest;
import com.ddd.client.ddd.request.DddWriteRequest;
import com.ddd.client.ddd.response.DddCalculateResponse;
import com.ddd.client.ddd.response.DddExternalReadResponse;
import com.ddd.client.ddd.response.DddReadResponse;
import com.ddd.client.ddd.response.DddRuleResponse;
import com.ddd.client.ddd.response.DddWriteResponse;

/**
 * DDD 公共模板的 HTTP 输入适配器。
 *
 * <p>负责协议校验、请求组装、调用单个应用服务和响应转换，不承载领域业务规则。</p>
 *
 * @author AIGenerator
 */
@RestController
@RequestMapping("/api/ddd")
public class DddController {
    @Autowired
    private DddWriteApplication writeApplication;

    @Autowired
    private DddReadApplication readApplication;

    @Autowired
    private DddCalculateApplication calculateApplication;

    @Autowired
    private DddRuleApplication ruleApplication;

    @Autowired
    private DddExternalReadApplication externalReadApplication;

    @Autowired
    private DddInputAssembler assembler;

    /**
     * 接收写模式请求，并将处理委托给写应用服务。
     *
     * @param request 写模式请求
     * @return 写模式处理结果
     *
     * @author AIGenerator
     */
    @PostMapping("/write")
    public Result<DddWriteResponse> write(@Valid @RequestBody DddWriteRequest request) {
        DddWriteResult result = writeApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    /**
     * 查询当前领域内的聚合数据。
     *
     * @param id 聚合根标识
     * @return 领域内查询结果
     *
     * @author AIGenerator
     */
    @GetMapping("/{id}")
    public Result<DddReadResponse> query(@PathVariable("id") String id) {
        DddReadResult view = readApplication.query(id);
        return Result.success(assembler.toResponse(view));
    }

    /**
     * 执行不依赖聚合和仓储的纯计算模式。
     *
     * @param request 纯计算请求
     * @return 纯计算结果
     *
     * @author AIGenerator
     */
    @PostMapping("/calculate")
    public Result<DddCalculateResponse> calculate(@Valid @RequestBody DddCalculateRequest request) {
        DddCalculateResult result = calculateApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    /**
     * 按领域规则执行计算模式。
     *
     * @param request 规则计算请求
     * @return 规则计算结果
     *
     * @author AIGenerator
     */
    @PostMapping("/rule")
    public Result<DddRuleResponse> calculateRule(@Valid @RequestBody DddRuleRequest request) {
        DddRuleResult result = ruleApplication.execute(assembler.toCommand(request));
        return Result.success(assembler.toResponse(result));
    }

    /**
     * 通过外部读取应用服务查询第三方数据。
     *
     * @param id 业务对象标识
     * @return 外部数据查询结果
     *
     * @author AIGenerator
     */
    @GetMapping("/{id}/external")
    public Result<DddExternalReadResponse> queryExternal(@PathVariable("id") String id) {
        DddExternalResult view = externalReadApplication.query(id);
        return Result.success(assembler.toResponse(view));
    }
}
