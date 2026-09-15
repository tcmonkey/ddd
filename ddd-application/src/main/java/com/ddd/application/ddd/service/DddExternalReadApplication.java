package com.ddd.application.ddd.service;

import com.ddd.application.ddd.adaptor.DddOutputAdaptor;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.model.ddd.DddModel;
import org.springframework.stereotype.Service;

/**
 * DDD 外部数据查询的应用服务模板。
 *
 * <p>这是“读模式查询外部系统”的示例：application 只依赖自身定义的端口，
 * output adaptor 负责外部调用与模型转换。</p>
 */
@Service
public final class DddExternalReadApplication {
    private final DddOutputAdaptor outputAdaptor;

    /**
     * 创建外部数据查询应用服务。
     *
     * @param outputAdaptor 外部数据查询端口
     */
    public DddExternalReadApplication(DddOutputAdaptor outputAdaptor) {
        this.outputAdaptor = outputAdaptor;
    }

    /**
     * 查询外部数据并转换为应用层结果。
     *
     * @param id 业务标识
     * @return 外部数据查询结果
     */
    public DddExternalResult query(String id) {
        DddModel external = outputAdaptor.queryById(id);
        return new DddExternalResult(external.id(), external.name(), external.category());
    }
}
