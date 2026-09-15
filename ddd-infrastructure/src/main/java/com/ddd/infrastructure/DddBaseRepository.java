package com.ddd.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.spring.repository.CrudRepository;

/**
 * 基础设施层统一的 MyBatis-Plus 仓储基类。
 *
 * <p>子类直接复用 {@link CrudRepository} 提供的 {@code save}、{@code updateById}、
 * {@code getById} 与 {@code list} 等通用 CRUD 能力。Mapper 由父类字段的 Spring
 * {@code @Autowired} 自动注入，子类无需重复传入 Mapper；不在 Mapper 中编写自定义 SQL。</p>
 *
 * @param <M> MyBatis-Plus Mapper 类型
 * @param <T> 持久化对象类型
 *
 * @author AIGenerator
 */
public abstract class DddBaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> {
}
