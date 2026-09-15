package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;

import com.ddd.application.ddd.adaptor.DddOutputAdaptor;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.common.result.Result;
import com.ddd.model.ddd.DddModel;

/**
 * DDD 外部数据查询的应用服务模板。
 *
 * <p>这是“读模式查询外部系统”的示例：application 只依赖自身定义的端口，
 * output adaptor 负责外部调用与模型转换。</p>
 *
 * @author AIGenerator
 */
@Service
public final class DddExternalReadApplication {
    private final DddOutputAdaptor outputAdaptor;

    public DddExternalReadApplication(DddOutputAdaptor outputAdaptor) {
        this.outputAdaptor = outputAdaptor;
    }

    /**
     * 查询外部数据并转换为应用层结果。
     *
     * @param id 业务标识
     * @return 外部数据查询结果
     *
     * @author AIGenerator
     */
    public Result<DddExternalResult> query(String id) {
        // 1. 调用输出适配端口获取项目内部模型。
        Result<DddModel> externalResult = outputAdaptor.queryById(id);
        if (!externalResult.success()) {
            return Result.failure(externalResult.code(), externalResult.message());
        }

        // 2. 将内部模型转换为应用层结果，避免向 Controller 泄漏模型。
        DddModel model = externalResult.data();
        DddExternalResult result = new DddExternalResult(model.id(), model.name(), model.category());
        return Result.success(result);
    }
}
