package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;

/**
 * DDD 写模式领域服务的不可变输入参数。
 *
 * @param aggregate 包含根实体和待处理子操作实体的输入聚合
 *
 * @author AIGenerator
 */
public record DddWriteParam(DddAggregate aggregate) {
    /**
     * 创建并初始化 DddWriteParam，校验或装配其所需输入。
     *
     * @param aggregate 待领域服务补齐和处理的输入聚合
     *
     * @author AIGenerator
     */
    public DddWriteParam {
        if (aggregate == null) {
            throw new DomainException(DomainErrorCode.DOMAIN_OPERATION_INVALID);
        }
    }
}
