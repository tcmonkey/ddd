package com.ddd.application.ddd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.param.DddDecisionParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.domain.ddd.service.DddWriteDomainService;

/**
 * DDD 写模式的应用服务模板。
 *
 * <p>仅将应用命令组装为包含输入聚合的领域参数，再委托领域服务完成加载、实体确认和保存。</p>
 *
 * @author AIGenerator
 */
@Service
public class DddWriteApplication {
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
    public DddWriteResult execute(DddWriteCommand command) {
        DddAggregate aggregate = DddAggregate.draft(command.id(), command.operationId(), command.baseValue(),
                command.ruleCode());
        DddDecisionParam decision = dddWriteDomainService.execute(new DddWriteParam(aggregate));
        return new DddWriteResult(command.id(), decision.operationId().value(), decision.value().value(),
                decision.currentValue().value(), decision.duplicate());
    }
}
