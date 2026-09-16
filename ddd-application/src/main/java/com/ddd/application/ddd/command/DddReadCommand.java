package com.ddd.application.ddd.command;

/**
 * DDD 域内读取应用命令。
 *
 * <p>即使仅携带一个聚合标识，Controller 调用 Application 时也必须使用命令对象，
 * 避免跨 Application 边界传递基本类型或裸字符串。</p>
 *
 * @param id 聚合根标识
 *
 * @author AIGenerator
 */
public record DddReadCommand(String id) {
}
