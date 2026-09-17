package com.ddd.adaptor.ddd.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import com.ddd.application.ddd.assembler.DddApplicationAssembler;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.service.DddWriteApplication;
import com.ddd.infrastructure.ddd.mysql.mapper.DddMapper;
import com.ddd.infrastructure.ddd.mysql.mapper.DddRuleMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddRulePO;
import com.ddd.model.ddd.DddWriteDO;
import com.ddd.start.Application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * 使用模板H2数据库验证保存后转换失败必须回滚，未访问真实MySQL。
 *
 * @author AIGenerator
 */
@SpringBootTest(classes = Application.class)
class WriteRollbackTest {
    @Autowired private DddWriteApplication application;
    @Autowired private DddMapper mapper;
    @Autowired private DddRuleMapper ruleMapper;
    @SpyBean private DddApplicationAssembler assembler;

    @Test
    void aggregateSaveIsRolledBackIfResponseAssemblyFails() {
        var rule = new DddRulePO();
        rule.setRuleCode("ROLLBACK_BOUNDARY");
        rule.setFactor(1);
        rule.setReason("回滚验证");
        ruleMapper.insert(rule);
        doThrow(new IllegalStateException("private-assembly-error"))
                .when(assembler)
                .toResult(any(DddWriteCommand.class), any(DddWriteDO.class));
        var result =
                assertDoesNotThrow(
                        () ->
                                application.write(
                                        new DddWriteCommand(
                                                "rollback-boundary-id",
                                                "rollback-operation",
                                                "ROLLBACK_BOUNDARY",
                                                10)));
        assertFalse(result.success());
        assertNull(mapper.selectById("rollback-boundary-id"));
    }
}
