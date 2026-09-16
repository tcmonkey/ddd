package com.ddd.adaptor.ddd.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ddd.adaptor.ddd.output.converter.DddOutputConverter;
import com.ddd.adaptor.ddd.output.model.DddExternalRequest;
import com.ddd.adaptor.ddd.output.model.DddExternalResponse;
import com.ddd.adaptor.exception.AdaptorErrorCode;
import com.ddd.adaptor.exception.AdaptorException;
import com.ddd.application.ddd.adaptor.DddOutputAdaptor;
import com.ddd.application.ddd.command.DddExternalReadCommand;
import com.ddd.common.result.Result;
import com.ddd.model.ddd.DddExternalReadDO;

/**
 * DDD 外部数据查询的 output adaptor 示例实现。
 *
 * <p>当前项目不接入真实第三方服务，因此构造模拟的第三方响应并完成转换；
 * 接入时只替换本类中的
 * 外部调用实现，application 端口和内部 DO 保持不变。</p>
 *
 * @author AIGenerator
 */
@Component
public class DddOutputAdaptorImpl implements DddOutputAdaptor {
    /**
     * 当前类的日志记录器。
     *
     * @author AIGenerator
     */
    private static final Logger LOG = LoggerFactory.getLogger(DddOutputAdaptorImpl.class);

    /**
     * 外部协议与内部模型的转换组件。
     *
     * @author AIGenerator
     */
    private final DddOutputConverter converter;

    /**
     * 创建并初始化 DddOutputAdaptorImpl，校验或装配其所需输入。
     *
     * @param converter 外部协议与内部模型的转换组件
     *
     * @author AIGenerator
     */
    public DddOutputAdaptorImpl(DddOutputConverter converter) {
        this.converter = converter;
    }

    /**
     * 查询第三方数据。
     *
     * <p>当前以模拟响应代替真实外部调用，仍返回完整的项目内部 DO，
     * 作为未来接入第三方服务的
     * 可替换模板。</p>
     *
     * @param dddExternalReadCommand 外部读取应用命令
     * @return 项目内部数据对象
     *
     * @author AIGenerator
     */
    @Override
    public Result<DddExternalReadDO> query(DddExternalReadCommand dddExternalReadCommand) {
        try {
            // 1. 将 Application 命令转换为第三方协议请求。
            DddExternalRequest externalRequest = converter.toExternalRequest(dddExternalReadCommand);

            // 2. 调用外部系统并取得外部协议响应。
            DddExternalResponse externalResponse = invokeRemote(externalRequest);

            // 3. 将外部协议转换为项目内部 DO。
            DddExternalReadDO dataObject = converter.toDataObject(externalResponse);
            return Result.success(dataObject);
        } catch (AdaptorException exception) {
            LOG.warn("DDD 外部适配失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 外部适配发生未预期异常", exception);
            return Result.failure(AdaptorErrorCode.ADAPTOR_PROCESS_FAILED);
        }
    }

    /**
     * 调用外部系统并取得其协议响应。
     *
     * <p>当前为可替换的模拟实现。真实 HTTP、RPC 或 MQ 客户端抛出的异常在
     * {@link #query(DddExternalReadCommand)}
     * 统一记录并映射为 adaptor 错误码。</p>
     *
     * @param request 第三方协议请求
     * @return 外部协议响应
     *
     * @author AIGenerator
     */
    private DddExternalResponse invokeRemote(DddExternalRequest request) {
        // 1. 校验调用外部系统所需的基础参数。
        if (request == null || request.sourceId() == null || request.sourceId().isBlank()) {
            throw new AdaptorException(AdaptorErrorCode.ADAPTOR_REQUEST_INVALID);
        }

        // 2. 当前用模拟响应占位，真实实现只替换这一处调用。
        String sourceId = request.sourceId();
        DddExternalResponse response = new DddExternalResponse(sourceId, "DDD_EXTERNAL_" + sourceId, "DEFAULT");
        return response;
    }
}
