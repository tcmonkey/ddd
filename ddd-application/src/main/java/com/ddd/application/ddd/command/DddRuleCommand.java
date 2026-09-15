package com.ddd.application.ddd.command;

/**
 * 规则与计算模式的应用层输入参数。
 *
 * @param ruleCode 选择或记录规则的编码
 * @param baseValue 参与规则或纯计算的原始数值
 *
 * @author AIGenerator
 */
public record DddRuleCommand(String ruleCode, int baseValue) {
}
