package com.ddd.infrastructure.ddd.mysql.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

/**
 * {@link com.ddd.domain.ddd.model.aggregate.DddAggregate} 的持久化对象。
 *
 * <p>对应主聚合唯一的 {@code ddd_data} 表；聚合主状态和领域实体快照保存在同一行。
 * 此类型不得暴露到基础设施层之外。</p>
 *
 * @author AIGenerator
 */
@TableName("ddd_data")
public class DddPO {
    /**
     * 聚合根唯一标识，对应主键 {@code id}。
     *
     * @author AIGenerator
     */
    @TableId("id")
    private String id;

    /**
     * 聚合根当前数值，对应 {@code current_value}，由领域行为变更。
     *
     * @author AIGenerator
     */
    private Integer currentValue;

    /**
     * 聚合根版本号，对应 {@code version}，由 MyBatis-Plus 乐观锁校验。
     *
     * @author AIGenerator
     */
    @Version
    private Long version;

    /**
     * 聚合内实体明细的 JSON 快照，对应 {@code entities_json}，用于重建幂等操作记录。
     *
     * @author AIGenerator
     */
    @TableField("entities_json")
    private String entitiesJson;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEntitiesJson() {
        return entitiesJson;
    }

    public void setEntitiesJson(String entitiesJson) {
        this.entitiesJson = entitiesJson;
    }
}
