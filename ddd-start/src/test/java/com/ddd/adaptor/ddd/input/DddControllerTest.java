package com.ddd.adaptor.ddd.input;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ddd.application.ddd.adaptor.DddOutputAdaptor;
import com.ddd.common.result.Result;
import com.ddd.infrastructure.ddd.mysql.mapper.DddMapper;
import com.ddd.infrastructure.ddd.mysql.mapper.DddRuleMapper;
import com.ddd.infrastructure.ddd.mysql.pojo.DddPO;
import com.ddd.infrastructure.ddd.mysql.pojo.DddRulePO;
import com.ddd.model.ddd.DddModel;
import com.ddd.start.Application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DDD 五种参考调用链的 HTTP 集成测试。
 *
 * @author AIGenerator
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class DddControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DddMapper dddMapper;

    @Autowired
    private DddRuleMapper dddRuleMapper;

    @Autowired
    private DddOutputAdaptor outputAdaptor;

    @Test
    void shouldCoverAllDddPatterns() throws Exception {
        mockMvc.perform(post("/api/ddd/rule").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ruleCode\":\"UNKNOWN\",\"baseValue\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("DOMAIN_RULE_NOT_FOUND"));

        insertRule("DEFAULT", 1, "默认规则");
        insertRule("DOUBLE", 2, "双倍规则");

        String request = """
                {"id":"ddd-001","operationId":"operation-001","ruleCode":"DOUBLE","baseValue":10}
                """;

        mockMvc.perform(post("/api/ddd/write").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.changedValue").value(20))
                .andExpect(jsonPath("$.data.currentValue").value(20))
                .andExpect(jsonPath("$.data.duplicate").value(false));

        mockMvc.perform(post("/api/ddd/write").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentValue").value(20))
                .andExpect(jsonPath("$.data.duplicate").value(true));

        String secondWriteRequest = """
                {"id":"ddd-001","operationId":"operation-001-2","ruleCode":"DEFAULT","baseValue":5}
                """;
        mockMvc.perform(post("/api/ddd/write").contentType(MediaType.APPLICATION_JSON)
                        .content(secondWriteRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.changedValue").value(5))
                .andExpect(jsonPath("$.data.currentValue").value(25))
                .andExpect(jsonPath("$.data.duplicate").value(false));

        mockMvc.perform(get("/api/ddd/ddd-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentValue").value(25))
                .andExpect(jsonPath("$.data.entities.length()").value(2));

        mockMvc.perform(post("/api/ddd/write").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentValue").value(25))
                .andExpect(jsonPath("$.data.duplicate").value(true));

        DddPO stored = dddMapper.selectById("ddd-001");
        assertEquals(25, stored.getCurrentValue());
        assertTrue(stored.getEntitiesJson().contains("operation-001"));
        assertTrue(stored.getEntitiesJson().contains("operation-001-2"));

        mockMvc.perform(post("/api/ddd/calculate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"baseValue\":8,\"factor\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.calculatedValue").value(16));

        mockMvc.perform(post("/api/ddd/rule").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ruleCode\":\"DOUBLE\",\"baseValue\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.factor").value(2))
                .andExpect(jsonPath("$.data.calculatedValue").value(16));

        mockMvc.perform(get("/api/ddd/ddd-001/external"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("ddd-001"))
                .andExpect(jsonPath("$.data.name").value("DDD_EXTERNAL_ddd-001"))
                .andExpect(jsonPath("$.data.category").value("DEFAULT"));

        Result<DddModel> invalidOutputResult = outputAdaptor.queryById(" ");
        assertFalse(invalidOutputResult.success());
        assertEquals("ADAPTOR_REQUEST_INVALID", invalidOutputResult.code());
    }

    /**
     * 只在测试环境写入规则样本，验证正式仓储的查询链路。
     *
     * @param ruleCode 规则编码
     * @param factor 计算因子
     * @param reason 规则说明
     *
     * @author AIGenerator
     */
    private void insertRule(String ruleCode, int factor, String reason) {
        DddRulePO rule = new DddRulePO();
        rule.setRuleCode(ruleCode);
        rule.setFactor(factor);
        rule.setReason(reason);
        dddRuleMapper.insert(rule);
    }
}
