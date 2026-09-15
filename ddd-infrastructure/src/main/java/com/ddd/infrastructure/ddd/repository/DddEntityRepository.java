package com.ddd.infrastructure.ddd.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ddd.infrastructure.DddBaseRepository;
import com.ddd.infrastructure.ddd.mysql.mapper.DddEntityMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddEntityPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DDD 领域实体持久化对象的基础设施仓储模板。
 *
 * <p>新增、更新等基础 CRUD 继承自 {@link DddBaseRepository}；按业务对象筛选使用
 * MyBatis-Plus 条件构造器，不编写 Mapper 自定义 SQL。</p>
 */
@Repository
public class DddEntityRepository
        extends DddBaseRepository<DddEntityMapper, DddEntityPO> {

    /**
     * 创建领域实体基础设施仓储。
     *
     * @param entityMapper 领域实体 Mapper
     */
    public DddEntityRepository(DddEntityMapper entityMapper) {
        super(entityMapper);
    }

    /**
     * 查询指定聚合根的全部领域实体持久化对象。
     *
     * @param id 聚合根标识
     * @return 对应的领域实体列表
     */
    public List<DddEntityPO> findById(String id) {
        return list(new LambdaQueryWrapper<DddEntityPO>()
                .eq(DddEntityPO::getId, id));
    }
}
