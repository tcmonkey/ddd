package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.application.exception.ApplicationErrorCode;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
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

    public DddWriteApplication(DddWriteDomainService dddWriteDomainService) {
        this.dddWriteDomainService = dddWriteDomainService;
    }

    /**
     * 将应用命令委托给领域写服务处理。
     *
     * <p>本层只传递原始命令数据；聚合及其根实体负责封装值对象和子操作实体。</p>
     *
     * @param command 写入应用命令
     * @return 写入或幂等重放结果
     *
     * @author AIGenerator
     */
    @Transactional
    public Result<DddWriteResult> execute(DddWriteCommand command) {
        try {
            // 1. 使用原始命令创建输入聚合，领域层负责封装值对象和实体。
            String id = command.id();
            String operationId = command.operationId();
            int baseValue = command.baseValue();
            String ruleCode = command.ruleCode();
            DddAggregate aggregate = DddAggregate.draft(id, operationId, baseValue, ruleCode);

            // 2. 组装领域参数并调用领域服务完成写入决策。
            DddWriteParam param = new DddWriteParam(aggregate);
            Result<DddWriteDO> domainResult = dddWriteDomainService.execute(param);
            if (!domainResult.success()) {
                return Result.failure(domainResult.code(), domainResult.message());
            }

            // 3. 将领域内部数据对象转换为应用层结果。
            DddWriteDO dataObject = domainResult.data();
            String decidedOperationId = dataObject.operationId();
            int changedValue = dataObject.changedValue();
            int currentValue = dataObject.currentValue();
            DddWriteResult result = new DddWriteResult(id, decidedOperationId, changedValue, currentValue,
                    dataObject.duplicate());
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
