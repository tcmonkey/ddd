package com.ddd.adaptor.ddd.output;

import com.ddd.adaptor.ddd.output.converter.DddOutputConverter;
import com.ddd.adaptor.ddd.output.model.DddExternalResponse;
import com.ddd.application.ddd.adaptor.DddOutputAdaptor;
import com.ddd.model.ddd.DddModel;
import org.springframework.stereotype.Component;

/**
 * DDD 外部数据查询的 output adaptor 示例实现。
 *
 * <p>当前项目不接入真实第三方服务，因此构造模拟的第三方响应并完成转换；接入时只替换本类中的
 * 外部调用实现，application 端口和内部模型保持不变。</p>
 */
@Component
public class DddOutputAdaptorImpl implements DddOutputAdaptor {
    private final DddOutputConverter converter;

    /**
     * 创建 DDD output adaptor。
     *
     * @param converter 第三方响应转换器
     */
    public DddOutputAdaptorImpl(DddOutputConverter converter) {
        this.converter = converter;
    }

    /**
     * 查询第三方数据。
     *
     * <p>当前以模拟响应代替真实外部调用，仍返回完整的项目内部模型，作为未来接入第三方服务的
     * 可替换模板。</p>
     *
     * @param id 业务标识
     * @return 项目内部模型
     */
    @Override
    public DddModel queryById(String id) {
        DddExternalResponse remoteResponse = new DddExternalResponse(
                id, "DDD_EXTERNAL_" + id, "DEFAULT");
        return converter.toModel(remoteResponse);
    }
}
