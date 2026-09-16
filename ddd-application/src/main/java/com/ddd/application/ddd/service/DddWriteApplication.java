package com.ddd.application.ddd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddd.application.exception.ApplicationErrorCode;
import com.ddd.application.ddd.assembler.DddApplicationAssembler;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.service.DddWriteDomainService;
import com.ddd.model.ddd.DddWriteDO;

/**
 * DDD 写模式的应用服务模板。
 *
 * <p>仅将应用命令组装为包含输入聚合的领域参数，
 * 再委托领域服务完成加载、实体确认和保存。</p>
 *
 * @author AIGenerator
 */
@Service
public class DddWriteApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DddWriteApplication.class);

    private final DddWriteDomainService dddWriteDomainService;
    private final DddApplicationAssembler assembler;

    public DddWriteApplication(DddWriteDomainService dddWriteDomainService, DddApplicationAssembler assembler) {
        this.dddWriteDomainService = dddWriteDomainService;
        this.assembler = assembler;
    }

    /**
     * 将应用命令委托给领域写服务处理。
     *
     * <p>本层只传递原始命令数据；聚合及其根实体负责封装值对象和子操作实体。</p>
     *
     * @param dddWriteCommand 写入应用命令
     * @return 写入或幂等重放结果
     *
     * @author AIGenerator
     */
    @Transactional
    public Result<DddWriteResult> write(DddWriteCommand dddWriteCommand) {
        try {
            // 1. 将应用命令组装为领域写入参数。
            DddWriteParam param = assembler.toDomainParam(dddWriteCommand);

            // 2. 调用领域服务完成写入决策。
            Result<DddWriteDO> domainResult = dddWriteDomainService.write(param);
            if (!domainResult.success()) {
                return Result.failure(domainResult.code(), domainResult.message());
            }

            // 3. 将领域内部数据对象转换为应用层结果。
            DddWriteDO dataObject = domainResult.data();
            DddWriteResult result = assembler.toResult(dddWriteCommand, dataObject);
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 写入应用组装失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 写入应用发生未预期异常", exception);
            return Result.failure(ApplicationErrorCode.APPLICATION_PROCESS_FAILED);
        }
    }
}
