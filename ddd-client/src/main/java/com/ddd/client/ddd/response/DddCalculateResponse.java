package com.ddd.client.ddd.response;

/**
 * DDD 纯计算模式的外部响应，不会变更聚合根状态。
 *
 * @author AIGenerator
 */
public record DddCalculateResponse(int calculatedValue) {
}
