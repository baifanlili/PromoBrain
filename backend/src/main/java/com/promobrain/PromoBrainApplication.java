package com.promobrain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class PromoBrainApplication {

    /**
     * 后端启动入口。
     * 第一版保持模块化单体，后续微服务演进时仍可以复用大部分业务包边界。
     */
    public static void main(String[] args) {
        SpringApplication.run(PromoBrainApplication.class, args);
    }
}
