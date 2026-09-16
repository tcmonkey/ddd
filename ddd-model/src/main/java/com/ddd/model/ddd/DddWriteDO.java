package com.ddd.model.ddd;

/**
 * 写领域服务向 Application 返回的内部数据对象。
 *
 * <p>该对象只承载跨层数据，不包含领域行为、持久化映射或外部协议注解。</p>
 *
 * @param operationId 操作幂等标识
 * @param changedValue 本次确认后的数值
 * @param currentValue 根实体当前累计值
 * @param reason 规则或领域决策说明
 * @param duplicate 是否为幂等重放
 *
 * @author AIGenerator
 */
public record DddWriteDO(
        String operationId,
        int changedValue,
        int currentValue,
        String reason,
        boolean duplicate) {
}
