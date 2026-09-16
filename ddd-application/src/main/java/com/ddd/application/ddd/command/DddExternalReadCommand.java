package com.ddd.application.ddd.command;

/**
 * 外部读取用例的应用命令。
 *
 * <p>即使当前只有一个业务标识，也必须使用命令对象跨 Application 边界传递，
 * 以支持校验、字段扩展和明确的调用语义。</p>
 *
 * @param id 待查询的业务对象标识
 *
 * @author AIGenerator
 */
public record DddExternalReadCommand(String id) {
}
