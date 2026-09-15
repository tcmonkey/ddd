package com.ddd.start.config;

import com.ddd.adaptor.ddd.input.assembler.DddInputAssembler;
import com.ddd.domain.ddd.repository.DddRuleRepository;
import com.ddd.infrastructure.ddd.repository.InMemoryDddRuleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * DDD 公共模板的 Spring 组合根。
 *
 * <p>领域模型不包含 Spring 注解；MyBatis-Plus 聚合根仓储由 {@code @Repository} 自动注册，
 * 本配置只装配时钟、规则数据和输入组装器等基础 Bean。</p>
 *
 * @author AIGenerator
 */
@Configuration
public class DddConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    DddRuleRepository dddRuleRepository() {
        return new InMemoryDddRuleRepository();
    }

    @Bean
    DddInputAssembler dddInputAssembler() {
        return new DddInputAssembler();
    }
}
