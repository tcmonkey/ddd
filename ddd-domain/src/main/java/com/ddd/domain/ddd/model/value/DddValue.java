package com.ddd.domain.ddd.model.value;

import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;

/**
 * 带有显式算术边界校验的非负领域值对象模板。
 *
 * @param value 非负领域数值
 * @author AIGenerator
 */
public record DddValue(int value) {
    /**
     * 创建并初始化 DddValue，校验或装配其所需输入。
     *
     * @param value 需校验的领域数值
     * @author AIGenerator
     */
    public DddValue {
        if (value < 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
    }

    /**
     * 创建严格大于零的领域值，不合法时抛出领域校验异常。
     *
     * @param value 需校验的领域数值
     * @return 处理或校验后的结果
     * @author AIGenerator
     */
    public static DddValue positive(int value) {
        // 1. 核验严格正数边界，非正数不能作为输入领域值。
        if (value <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
        // 2. 创建已满足输入边界的值对象。
        return new DddValue(value);
    }

    /**
     * 累加两个领域值，并通过精确算术检查整数溢出。
     *
     * @param other 参与累加的领域值
     * @return 处理或校验后的结果
     * @author AIGenerator
     */
    public DddValue add(DddValue other) {
        return new DddValue(Math.addExact(value, other.value));
    }

    /**
     * 使用正数因子计算领域值，并检查整数溢出。
     *
     * @param factor 正数规则计算因子
     * @return 处理或校验后的结果
     * @author AIGenerator
     */
    public DddValue multiply(int factor) {
        // 1. 拒绝非正规则因子，避免改变领域值的有效范围。
        if (factor <= 0) {
            throw new DomainException(DomainErrorCode.DOMAIN_VALUE_INVALID);
        }
        // 2. 精确乘法检查溢出，新值再次接受值对象不变量校验。
        return new DddValue(Math.multiplyExact(value, factor));
    }
}
