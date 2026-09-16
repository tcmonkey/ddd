package com.ddd.adaptor.ddd.output.converter;

import org.springframework.stereotype.Component;

import com.ddd.adaptor.ddd.output.model.DddExternalRequest;
import com.ddd.adaptor.ddd.output.model.DddExternalResponse;
import com.ddd.adaptor.exception.AdaptorErrorCode;
import com.ddd.adaptor.exception.AdaptorException;
import com.ddd.application.ddd.command.DddExternalReadCommand;
import com.ddd.model.ddd.DddExternalReadDO;

/**
 * Application Command、第三方协议与项目内部 DO 的转换器。
 *
 * <p>第三方字段适配集中在 output converter，避免第三方协议进入 application。</p>
 *
 * @author AIGenerator
 */
@Component
public class DddOutputConverter {
    /**
     * 将 Application Command 转换为第三方协议请求。
     *
     * @param command 外部读取应用命令
     * @return 第三方协议请求
     *
     * @author AIGenerator
     */
    public DddExternalRequest toExternalRequest(DddExternalReadCommand command) {
        // 1. 校验 Application Command 是否具备外部调用所需的标识。
        if (command == null || command.id() == null || command.id().isBlank()) {
            throw new AdaptorException(AdaptorErrorCode.ADAPTOR_REQUEST_INVALID);
        }

        // 2. 将项目内部命令映射为第三方协议请求。
        DddExternalRequest request = new DddExternalRequest(command.id());
        return request;
    }

    /**
     * 将第三方响应转换为项目内部 DO。
     *
     * @param response 第三方响应
     * @return 项目内部数据对象
     *
     * @author AIGenerator
     */
    public DddExternalReadDO toDataObject(DddExternalResponse response) {
        // 1. 校验外部响应是否具备转换为内部 DO 的必要字段。
        if (response == null || response.sourceId() == null || response.sourceId().isBlank()) {
            throw new AdaptorException(AdaptorErrorCode.ADAPTOR_EXTERNAL_RESPONSE_INVALID);
        }

        // 2. 将第三方协议字段隔离并转换为项目内部 DO。
        DddExternalReadDO dataObject = new DddExternalReadDO(response.sourceId(), response.sourceName(),
                response.sourceCategory());
        return dataObject;
    }
}
