package com.ddd.application.ddd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ddd.application.exception.ApplicationErrorCode;
import com.ddd.application.ddd.assembler.DddApplicationAssembler;
import com.ddd.application.ddd.command.DddReadCommand;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.param.DddReadParam;
import com.ddd.domain.ddd.repository.DddRepository;

/**
 * DDD 域内读模式的应用服务模板。
 *
 * <p>仅转换聚合根数据，不承载业务规则。</p>
 *
 * @author AIGenerator
 */
@Service
public final class DddReadApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DddReadApplication.class);

    private final DddRepository dddRepository;
    private final DddApplicationAssembler assembler;

    public DddReadApplication(DddRepository dddRepository, DddApplicationAssembler assembler) {
        this.dddRepository = dddRepository;
        this.assembler = assembler;
    }

    /**
     * 按聚合根标识查询域内数据，并将领域实体转换为应用层结果。
     *
     * @param command 域内读取应用命令
     * @return 域内读取结果
     *
     * @author AIGenerator
     */
    public Result<DddReadResult> query(DddReadCommand command) {
        try {
            // 1. 将应用命令组装为领域读取参数。
            DddReadParam param = assembler.toDomainParam(command);

            // 2. 按领域参数读取完整聚合。
            DddAggregate aggregate = dddRepository.findById(param);

            // 3. 将聚合转换为应用层只读结果。
            DddReadResult result = assembler.toResult(aggregate);
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 域内查询失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 域内查询发生未预期异常", exception);
            return Result.failure(ApplicationErrorCode.APPLICATION_PROCESS_FAILED);
        }
    }
}
