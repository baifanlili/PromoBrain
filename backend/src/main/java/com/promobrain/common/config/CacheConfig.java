package com.promobrain.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 第二版本地缓存配置。
 * Caffeine 用作热点广告计划、商品、素材的一级缓存；Redis 仍负责跨实例共享缓存和预算扣减。
 */
@Configuration
public class CacheConfig {

    /**
     * 注册 Caffeine CacheManager。
     * 第一阶段先统一 TTL 和容量，后续可按 cacheName 拆不同策略。
     */
    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "demoSnapshot",
                "adProduct",
                "adCampaign",
                "hybridSearch"
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        return manager;
    }
}

