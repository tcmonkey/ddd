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
        Result<DddModel> externalResult = outputAdaptor.queryById(id);
        return externalResult.map(external -> new DddExternalResult(external.id(), external.name(),
                external.category()));
    }
}
