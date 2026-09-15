package com.ddd.domain.ddd.model.param;

import com.ddd.domain.ddd.model.aggregate.DddAggregate;

/**
 * DDD 写模式领域服务的不可变输入参数。
 *
 * @param aggregate 包含根实体和待处理子操作实体的输入聚合
 *
 * @author AIGenerator
 */
public record DddWriteParam(DddAggregate aggregate) {
    public DddWriteParam {
        if (aggregate == null) {
            throw new IllegalArgumentException("aggregate must not be null");
        }
    }
}
