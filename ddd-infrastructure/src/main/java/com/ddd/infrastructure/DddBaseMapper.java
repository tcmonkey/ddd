package com.ddd.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 基础设施层持久化对象共用的 MyBatis-Plus Mapper 基类。
 *
 * @param <T> 数据库持久化对象类型
 * @author AIGenerator
 */
public interface DddBaseMapper<T> extends BaseMapper<T> {
}
