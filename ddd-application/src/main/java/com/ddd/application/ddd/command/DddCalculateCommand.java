package com.ddd.application.ddd.command;

/**
 * DDD 纯计算模式的应用层输入参数。
 *
 * @author AIGenerator
 */
public record DddCalculateCommand(int baseValue, int factor) {
}
