package com.ddd.model.ddd;

/**
 * Output Adaptor 向 Application 返回的外部查询内部数据对象。
 *
 * <p>该对象已经完成第三方协议防腐转换，client 不得依赖它。</p>
 *
 * @param id 业务对象标识
 * @param name 对象名称
 * @param category 对象分类
 *
 * @author AIGenerator
 */
public record DddExternalReadDO(
        String id,
        String name,
        String category) {
}
