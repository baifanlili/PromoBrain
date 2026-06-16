package com.promobrain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

    /**
     * 加载预算扣减 Lua 脚本。
     * 脚本作为 Bean 注入，可以避免每次扣减时重复读取文件，也方便后续单元测试替换。
     */
    @Bean
    DefaultRedisScript<Long> budgetDeductScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/budget_deduct.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
