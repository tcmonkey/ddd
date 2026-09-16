package com.ddd.domain.ddd.repository;

import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;

/**
 * DDD 聚合根的领域仓储模板。
 *
 * <p>仓储按标识查询可接收基础类型，写入接收聚合根；不泄漏持久化对象或 {@code Optional}。
 * 数据不存在时，查询实现返回尚未持久化的空聚合根，调用方可按正常业务流程继续处理。</p>
 *
 * @author AIGenerator
 */
public interface DddRepository {
    /**
     * 按业务标识查询聚合根。
     *
     * @param id 聚合根业务标识
     * @return 已恢复的聚合根；数据不存在时返回当前值为零的空聚合根
     * @throws DomainException 当业务标识不满足领域约束时抛出
     *
     * @author AIGenerator
     */
    DddAggregate findById(String id);

    /**
     * 保存聚合根及其完整实体快照。
     *
     * @param aggregate 待保存的聚合根
     * @return 保存成功返回 {@code true}；乐观锁冲突或持久化失败返回 {@code false}
     *
     * @author AIGenerator
     */
    Boolean save(DddAggregate aggregate);
}
