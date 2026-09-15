package com.ddd.infrastructure.ddd.repository;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.value.DddIdValue;
import com.ddd.domain.ddd.repository.DddRepository;

/**
 * 内存版 DDD 聚合根仓储模板，用于脱离数据库的局部测试。
 *
 * <p>生产运行使用 MyBatis-Plus 实现；该实现保留相同的聚合根返回和布尔保存约定。</p>
 *
 * @author AIGenerator
 */
public final class InMemoryDddRepository implements DddRepository {
    private final ConcurrentMap<DddIdValue, DddAggregate> aggregates = new ConcurrentHashMap<>();

    @Override
    public DddAggregate findById(DddIdValue id) {
        DddAggregate aggregate = aggregates.get(id);
        return aggregate == null ? DddAggregate.open(id) : aggregate.copy();
    }

    @Override
    public Boolean save(DddAggregate aggregate) {
        aggregates.compute(aggregate.id(), (id, stored) -> {
            if (stored == null) {
                if (aggregate.version() != 1) {
                    throw new ConcurrentModificationException("新聚合根版本必须为 1");
                }
            } else if (stored.version() != aggregate.version() - 1) {
                throw new ConcurrentModificationException("DDD 聚合根版本冲突");
            }
            return aggregate.copy();
        });
        return true;
    }
}
