package com.ddd.start.config.domain;

import com.ddd.domain.annotation.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * 领域服务的定向扫描配置。
 *
 * <p>在不向 domain 模块引入 Spring 依赖的前提下，将标记的领域服务注册为 Bean。</p>
 */
@Configuration
@ComponentScan(
        basePackages = "com.ddd.domain",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = DomainService.class))
public class DomainServiceScanConfiguration {
}
