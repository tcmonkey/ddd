package com.ddd.domain.ddd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddd.common.result.Result;
import com.ddd.domain.annotation.DomainService;
import com.ddd.domain.ddd.exception.DomainErrorCode;
import com.ddd.domain.ddd.exception.DomainException;
import com.ddd.domain.ddd.model.param.DddCalculateParam;
import com.ddd.domain.ddd.model.value.DddValue;
import com.ddd.model.ddd.DddCalculateDO;

/**
 * DDD 纯计算模式的领域服务模板。
 *
 * <p>不检索聚合根或仓储，也不变更领域状态；所有计算逻辑都收敛在领域服务中。</p>
 *
 * @author AIGenerator
 */
@DomainService
public final class DddCalculateDomainService {
    /**
     * 当前类的日志记录器。
     *
     * @author AIGenerator
     */
    private static final Logger LOG = LoggerFactory.getLogger(DddCalculateDomainService.class);

    /**
     * 根据输入值和计算因子得到结果。
     *
     * @param param 纯计算领域参数
     * @return 计算结果操作包装
     *
     * @author AIGenerator
     */
    public Result<DddCalculateDO> calculate(DddCalculateParam param) {
        try {
            // 1. 将原始数值封装为满足不变量的领域值对象。
            DddValue base = DddValue.positive(param.baseValue());

            // 2. 使用领域值对象完成计算并返回领域结果。
            DddValue calculatedValue = base.multiply(param.factor());
            DddCalculateDO result = new DddCalculateDO(calculatedValue.value());
            return Result.success(result);
        } catch (DomainException exception) {
            LOG.warn("DDD 纯计算失败, code={}", exception.errorCode().code());
            return Result.failure(exception.errorCode());
        } catch (Exception exception) {
            LOG.error("DDD 纯计算发生未预期异常", exception);
            return Result.failure(DomainErrorCode.DOMAIN_PROCESS_FAILED);
        }
    }
}
