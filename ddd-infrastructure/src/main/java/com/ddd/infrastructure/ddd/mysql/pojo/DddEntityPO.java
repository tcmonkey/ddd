package com.ddd.infrastructure.ddd.mysql.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;

/** DDD 领域实体的持久化对象模板。 */
@TableName("ddd_entity")
public class DddEntityPO {
    @TableId("operation_id")
    private String operationId;
    private String id;
    private Integer businessValue;
    private String ruleCode;
    private Instant occurredAt;

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getBusinessValue() { return businessValue; }
    public void setBusinessValue(Integer businessValue) { this.businessValue = businessValue; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
