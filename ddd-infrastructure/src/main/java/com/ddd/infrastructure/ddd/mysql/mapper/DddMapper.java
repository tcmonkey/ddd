package com.ddd.infrastructure.ddd.mysql.mapper;

import com.ddd.infrastructure.DddBaseMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddPO;

/**
 * DDD 聚合根 Mapper 模板。
 *
 * <p>由 {@code Application} 上的 {@code @MapperScan} 集中注册，无需在接口上逐个标注
 * {@code @Mapper}。</p>
 *
 * @author AIGenerator
 */
public interface DddMapper extends DddBaseMapper<DddPO> {
}
