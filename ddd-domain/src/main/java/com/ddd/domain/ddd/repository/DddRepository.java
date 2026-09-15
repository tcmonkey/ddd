package com.ddd.domain.ddd.repository;

import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.value.DddIdValue;

/**
 * DDD 聚合根的领域仓储模板。
 *
 * <p>仓储边界只传递聚合根，不泄漏持久化对象或 {@code Optional}。数据不存在时，查询实现返回一个
 * 尚未持久化的空聚合根，调用方可按正常业务流程继续处理。</p>
 *
 * @author AIGenerator
 */
public interface DddRepository {
    /**
     * 按业务标识查询聚合根。
     *
     * @param id 业务标识值对象
     * @return 已恢复的聚合根；数据不存在时返回当前值为零的空聚合根
     *
     * @author AIGenerator
     */
    DddAggregate findById(DddIdValue id);

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
