package com.ddd.domain.ddd.service;

import com.ddd.domain.annotation.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.common.result.Result;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.value.DddValue;

/**
 * DDD 纯计算模式的领域服务模板。
 *
 * <p>不检索聚合根或仓储，也不变更领域状态；所有计算逻辑都收敛在领域服务中。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddCalculateDomainService {
    private static final Logger LOG = LoggerFactory.getLogger(DddCalculateDomainService.class);

    /**
     * 根据输入值和计算因子得到结果。
     *
     * @param baseValue 输入值
     * @param factor 计算因子
     * @return 计算结果操作包装
     *
     * @author AIGenerator
     */
    public Result<DddValue> calculate(int baseValue, int factor) {
        try {
            return Result.success(DddValue.positive(baseValue).multiply(factor));
        } catch (DomainException exception) {
            LOG.warn("DDD 纯计算失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 纯计算发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
