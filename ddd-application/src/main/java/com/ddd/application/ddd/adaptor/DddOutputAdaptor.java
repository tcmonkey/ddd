package com.ddd.application.ddd.adaptor;

import com.ddd.model.ddd.DddModel;

/**
 * DDD 外部数据查询端口模板。
 *
 * <p>端口由 application 定义，具体第三方协议与调用细节由 adaptor output 实现。</p>
 */
public interface DddOutputAdaptor {
    /**
     * 查询指定标识对应的外部数据。
     *
     * @param id 业务标识
     * @return 转换后的项目内部模型
     */
    DddModel queryById(String id);
}
