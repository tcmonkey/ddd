package com.ddd.adaptor.ddd.output.model;

/**
 * 第三方服务响应模型示例。
 *
 * <p>接入真实服务后，此对象应按第三方协议字段定义，不能泄漏到 application 或 domain。</p>
 *
 * @param sourceId 第三方协议中的对象标识
 * @param sourceName 第三方协议中的对象名称
 * @param sourceCategory 第三方协议中的对象分类
 *
 * @author AIGenerator
 */
public record DddExternalResponse(
        String sourceId,
        String sourceName,
        String sourceCategory) {
}
