package com.ddd.infrastructure.ddd.mysql.mapper;

import com.ddd.infrastructure.DddBaseMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddEntityPO;

/**
 * DDD 领域实体 Mapper 模板。
 *
 * <p>由 {@code Application} 上的 {@code @MapperScan} 集中注册；本示例不编写手工 SQL。</p>
 */
public interface DddEntityMapper extends DddBaseMapper<DddEntityPO> {
}
