package com.ddd.application.ddd.command;

/**
 * DDD 纯计算模式的应用层输入参数。
 *
 * @param baseValue 参与规则或纯计算的原始数值
 * @param factor 乘法计算因子，必须为正数
 *
 * @author AIGenerator
 */
public record DddCalculateCommand(int baseValue, int factor) {
}
