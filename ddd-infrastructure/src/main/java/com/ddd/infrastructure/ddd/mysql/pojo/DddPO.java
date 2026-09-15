package com.ddd.infrastructure.ddd.mysql.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

/**
 * DDD 聚合根持久化对象模板，不得暴露到基础设施层之外。
 *
 * @author AIGenerator
 */
@TableName("ddd_data")
public class DddPO {
    @TableId("id")
    private String id;
    private Integer currentValue;
    @Version
    private Long version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getCurrentValue() { return currentValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
