package com.ddd.application.ddd.assembler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ddd.application.ddd.command.DddCalculateCommand;
import com.ddd.application.ddd.command.DddRuleCommand;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.result.DddCalculateResult;
import com.ddd.application.ddd.result.DddExternalResult;
import com.ddd.application.ddd.result.DddReadResult;
import com.ddd.application.ddd.result.DddRuleResult;
import com.ddd.application.ddd.result.DddWriteResult;
import com.ddd.domain.ddd.model.aggregate.DddAggregate;
import com.ddd.domain.ddd.model.entity.DddEntity;
import com.ddd.domain.ddd.model.entity.DddOperationEntity;
import com.ddd.domain.ddd.model.param.DddCalculateParam;
import com.ddd.domain.ddd.model.param.DddRuleParam;
import com.ddd.domain.ddd.model.param.DddWriteParam;
import com.ddd.model.ddd.DddCalculateDO;
import com.ddd.model.ddd.DddExternalReadDO;
import com.ddd.model.ddd.DddRuleCalculateDO;
import com.ddd.model.ddd.DddWriteDO;

/**
 * Application 的参数组装与结果转换防腐层。
 *
 * <p>Controller assembler 只处理 HTTP 协议，本类只处理 Application Command、
 * Domain Param、Domain/OutAdaptor DO 与 Application Result 的转换，
 * 避免应用服务混入重复的对象组装逻辑。</p>
 *
 * @author AIGenerator
 */
@Component
public class DddApplicationAssembler {
    /**
     * 将写入应用命令转换为领域写入参数。
     *
     * @param command 写入应用命令
     * @return 领域写入参数
     *
     * @author AIGenerator
     */
    public DddWriteParam toDomainParam(DddWriteCommand command) {
        // 1. 读取应用命令中的原始写入字段。
        String id = command.id();
        String operationId = command.operationId();
        int baseValue = command.baseValue();
        String ruleCode = command.ruleCode();

        // 2. 委托聚合工厂创建领域输入聚合。
        DddAggregate aggregate = DddAggregate.draft(id, operationId, baseValue, ruleCode);
        DddWriteParam param = new DddWriteParam(aggregate);
        return param;
    }

    /**
     * 将规则计算应用命令转换为领域参数。
     *
     * @param command 规则计算应用命令
     * @return 规则计算领域参数
     *
     * @author AIGenerator
     */
    public DddRuleParam toDomainParam(DddRuleCommand command) {
        // 1. 读取规则计算所需字段。
        String ruleCode = command.ruleCode();
        int baseValue = command.baseValue();

        // 2. 组装领域参数。
        DddRuleParam param = new DddRuleParam(ruleCode, baseValue);
        return param;
    }

    /**
     * 将纯计算应用命令转换为领域参数。
     *
     * @param command 纯计算应用命令
     * @return 纯计算领域参数
     *
     * @author AIGenerator
     */
    public DddCalculateParam toDomainParam(DddCalculateCommand command) {
        // 1. 读取纯计算所需字段。
        int baseValue = command.baseValue();
        int factor = command.factor();

        // 2. 组装领域参数。
        DddCalculateParam param = new DddCalculateParam(baseValue, factor);
        return param;
    }

    /**
     * 将写领域输出转换为应用结果。
     *
     * @param command 原始写入应用命令
     * @param dataObject 领域输出数据对象
     * @return 写入应用结果
     *
     * @author AIGenerator
     */
    public DddWriteResult toResult(DddWriteCommand command, DddWriteDO dataObject) {
        // 1. 保留应用命令中的业务对象标识。
        String id = command.id();

        // 2. 从领域 DO 提取应用响应字段。
        String operationId = dataObject.operationId();
        int changedValue = dataObject.changedValue();
        int currentValue = dataObject.currentValue();
        boolean duplicate = dataObject.duplicate();

        // 3. 组装应用层结果。
        DddWriteResult result = new DddWriteResult(id, operationId, changedValue, currentValue, duplicate);
        return result;
    }

    /**
     * 将规则计算领域输出转换为应用结果。
     *
     * @param dataObject 领域输出数据对象
     * @return 规则计算应用结果
     *
     * @author AIGenerator
     */
    public DddRuleResult toResult(DddRuleCalculateDO dataObject) {
        // 1. 从领域 DO 提取规则计算字段。
        String ruleCode = dataObject.ruleCode();
        int factor = dataObject.factor();
        int calculatedValue = dataObject.calculatedValue();
        String reason = dataObject.reason();

        // 2. 组装应用层结果。
        DddRuleResult result = new DddRuleResult(ruleCode, factor, calculatedValue, reason);
        return result;
    }

    /**
     * 将纯计算领域输出转换为应用结果。
     *
     * @param dataObject 领域输出数据对象
     * @return 纯计算应用结果
     *
     * @author AIGenerator
     */
    public DddCalculateResult toResult(DddCalculateDO dataObject) {
        // 1. 从领域 DO 提取计算值。
        int calculatedValue = dataObject.calculatedValue();

        // 2. 组装应用层结果。
        DddCalculateResult result = new DddCalculateResult(calculatedValue);
        return result;
    }

    /**
     * 将外部读取 DO 转换为应用结果。
     *
     * @param dataObject 外部读取内部数据对象
     * @return 外部读取应用结果
     *
     * @author AIGenerator
     */
    public DddExternalResult toResult(DddExternalReadDO dataObject) {
        // 1. 从输出 DO 提取展示所需字段。
        String id = dataObject.id();
        String name = dataObject.name();
        String category = dataObject.category();

        // 2. 组装应用层结果。
        DddExternalResult result = new DddExternalResult(id, name, category);
        return result;
    }

    /**
     * 将领域聚合转换为域内读取应用结果。
     *
     * @param aggregate 已恢复的领域聚合
     * @return 域内读取应用结果
     *
     * @author AIGenerator
     */
    public DddReadResult toResult(DddAggregate aggregate) {
        // 1. 获取聚合根实体的当前状态。
        DddEntity entity = aggregate.entity();

        // 2. 将聚合内子实体转换为应用层视图。
        List<DddReadResult.EntityView> entities = entity.operationEntities().stream()
                .map(this::toEntityView)
                .toList();

        // 3. 组装应用层读取结果。
        String id = entity.id().value();
        int currentValue = entity.currentValue().value();
        DddReadResult result = new DddReadResult(id, currentValue, entities);
        return result;
    }

    /**
     * 将领域子实体转换为应用层只读视图。
     *
     * @param entity 领域子实体
     * @return 应用层子实体视图
     *
     * @author AIGenerator
     */
    private DddReadResult.EntityView toEntityView(DddOperationEntity entity) {
        // 1. 从领域子实体读取展示所需字段。
        String operationId = entity.operationId().value();
        int value = entity.value().value();
        String ruleCode = entity.ruleCode();

        // 2. 组装应用层子实体视图。
        DddReadResult.EntityView view = new DddReadResult.EntityView(operationId, value, ruleCode,
                entity.occurredAt());
        return view;
    }
}
