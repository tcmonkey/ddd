package com.ddd.model.ddd;

/**
 * DDD 外部查询结果的项目内部通用模型。
 *
 * <p>该模型用于 application 与 output adaptor 之间传递外部查询结果；client 模块不得依赖它。</p>
 *
 * @param id 业务对象标识
 * @param name 对象名称
 * @param category 对象分类
 *
 * @author AIGenerator
 */
public record DddModel(
        String id,
        String name,
        String category) {
}
