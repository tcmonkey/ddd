package com.ddd.domain.ddd.model.result;

import com.ddd.domain.ddd.model.value.DddOperationIdValue;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * 写领域服务成功完成决策后返回的领域结果。
 *
 * <p>该对象只表达领域事实；成功与失败由外层 {@code Result} 表达，
 * 避免将错误码或接口协议混入
 * 领域决策模型。</p>
 *
 * @param operationId 一次写操作的幂等标识
 * @param value 领域决策得到的本次数值
 * @param currentValue 保存前或保存后的根实体当前值
 * @param reason 规则或领域决策的说明
 * @param duplicate 是否为幂等重放
 *
 * @author AIGenerator
 */
public record DddWriteDecision(
        DddOperationIdValue operationId,
        DddValue value,
        DddValue currentValue,
        String reason,
        boolean duplicate) {
    /**
     * 创建正常写入后的领域决策。
     *
     * @param operationId 操作标识
     * @param value 本次计算值
     * @param currentValue 根实体当前值
     * @param reason 规则说明
     * @return 正常写入决策
     *
     * @author AIGenerator
     */
    public static DddWriteDecision written(DddOperationIdValue operationId, DddValue value,
                                           DddValue currentValue, String reason) {
        return new DddWriteDecision(operationId, value, currentValue, reason, false);
    }

    /**
     * 创建幂等重放后的领域决策。
     *
     * @param operationId 操作标识
     * @param value 历史计算值
     * @param currentValue 根实体当前值
     * @param reason 重放说明
     * @return 幂等重放决策
     *
     * @author AIGenerator
     */
    public static DddWriteDecision duplicate(DddOperationIdValue operationId, DddValue value,
                                             DddValue currentValue, String reason) {
        return new DddWriteDecision(operationId, value, currentValue, reason, true);
    }
}
