package com.ddd.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要由外层组合根注册的纯领域服务。
 *
 * <p>该注解不依赖 Spring，保证 domain 模块保持框架无关。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainService {
}
