package com.ddd.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Java DDD 参考工程的框架启动入口。 */
@SpringBootApplication(scanBasePackages = "com.ddd")
@MapperScan("com.ddd.infrastructure.**.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
