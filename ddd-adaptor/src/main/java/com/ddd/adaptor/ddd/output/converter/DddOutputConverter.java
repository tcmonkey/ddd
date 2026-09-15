package com.ddd.adaptor.ddd.output.converter;

import org.springframework.stereotype.Component;

import com.ddd.adaptor.exception.AdaptorErrorCode;
import com.ddd.adaptor.exception.AdaptorException;
import com.ddd.adaptor.ddd.output.model.DddExternalResponse;
import com.ddd.model.ddd.DddModel;

/**
 * 第三方响应到项目内部模型的转换器。
 *
 * <p>第三方字段适配集中在 output converter，避免第三方协议进入 application。</p>
 *
 * @author AIGenerator
 */
@Component
public class DddOutputConverter {
    /**
     * 将第三方响应转换为项目内部模型。
     *
     * @param response 第三方响应
     * @return 项目内部模型
     *
     * @author AIGenerator
     */
    public DddModel toModel(DddExternalResponse response) {
        // 1. 校验外部响应是否具备转换为内部模型的必要字段。
        if (response == null || response.sourceId() == null || response.sourceId().isBlank()) {
            throw new AdaptorException(AdaptorErrorCode.ADAPTOR_EXTERNAL_RESPONSE_INVALID);
        }

        // 2. 将第三方协议字段隔离并转换为项目内部模型。
        DddModel model = new DddModel(response.sourceId(), response.sourceName(), response.sourceCategory());
        return model;
    }
}
