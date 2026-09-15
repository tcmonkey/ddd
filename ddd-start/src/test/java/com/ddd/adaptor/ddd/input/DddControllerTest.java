package com.ddd.adaptor.ddd.input;

import com.ddd.start.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void shouldCoverAllDddPatterns() throws Exception {
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

        mockMvc.perform(post("/api/ddd/write").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"ddd-001\",\"operationId\":\"operation-001-2\",\"ruleCode\":\"DEFAULT\",\"baseValue\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.changedValue").value(5))
                .andExpect(jsonPath("$.data.currentValue").value(25))
                .andExpect(jsonPath("$.data.duplicate").value(false));

        mockMvc.perform(get("/api/ddd/ddd-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentValue").value(25))
                .andExpect(jsonPath("$.data.entities.length()").value(2));

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
    }
}
