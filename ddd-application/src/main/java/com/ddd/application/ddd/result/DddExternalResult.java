package com.ddd.application.ddd.result;

/**
 * DDD 外部数据查询的应用层结果。
 *
 * @param id 业务对象标识
 * @param name 对象名称
 * @param category 对象分类
 *
 * @author AIGenerator
 */
public record DddExternalResult(
        String id,
        String name,
        String category) {
}
