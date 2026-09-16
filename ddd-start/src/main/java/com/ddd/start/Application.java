package com.ddd.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Java DDD 参考工程的框架启动入口。
 *
 * @author AIGenerator
 */
@SpringBootApplication(scanBasePackages = "com.ddd")
@MapperScan("com.ddd.infrastructure.**.mapper")
public class Application {

    /**
     * 启动 Java DDD 模板的 Spring Boot 应用。
     *
     * @param args 启动命令行参数
     *
     * @author AIGenerator
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
