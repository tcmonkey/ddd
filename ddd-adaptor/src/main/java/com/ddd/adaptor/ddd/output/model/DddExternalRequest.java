package com.ddd.adaptor.ddd.output.model;

/**
 * 第三方服务请求模型示例。
 *
 * <p>该对象只匹配第三方协议，由 output converter 从 Application Command 转换得到，
 * 不得进入 application 或 domain。</p>
 *
 * @param sourceId 第三方协议要求的对象标识
 *
 * @author AIGenerator
 */
public record DddExternalRequest(String sourceId) {
}
