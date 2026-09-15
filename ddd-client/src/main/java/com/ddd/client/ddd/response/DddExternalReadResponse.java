package com.ddd.client.ddd.response;

/**
 * DDD 外部数据查询的 HTTP 响应。
 *
 * @param id 业务对象标识
 * @param name 对象名称
 * @param category 对象分类
 *
 * @author AIGenerator
 */
public record DddExternalReadResponse(
        String id,
        String name,
        String category) {
}
