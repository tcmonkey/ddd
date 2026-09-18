package com.ddd.start.config.database;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 由外层启动模块维护的 MyBatis-Plus 配置。
 *
 * @author AIGenerator
 */
@Configuration
public class MybatisPlusConfiguration {
    /**
     * 创建包含乐观锁能力的 MyBatis-Plus 拦截器。
     *
     * @return 已注册乐观锁插件的拦截器
     * @author AIGenerator
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 1. 创建当前服务持久化拦截器链。
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 2. 注册版本锁拦截器，更新时核验旧版本以识别并发冲突。
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
