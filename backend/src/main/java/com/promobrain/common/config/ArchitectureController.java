package com.promobrain.common.config;

import com.promobrain.common.response.Result;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArchitectureController {

    private final PromoBrainProperties properties;
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public ArchitectureController(
            PromoBrainProperties properties,
            StringRedisTemplate redisTemplate,
            RabbitTemplate rabbitTemplate
    ) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 后端健康检查。
     * 只检查进程可用性，不强依赖 MySQL、Redis、RabbitMQ，避免本地中间件没启动时误判后端不可用。
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        return Result.ok(Map.of(
                "service", "promobrain-backend",
                "status", "ok",
                "architectureStage", "v2-monolith-plus"
        ));
    }

    /**
     * 第一版架构说明接口。
     * 前端、README 截图和面试演示都可以用它展示当前已经接入的技术边界。
     */
    @GetMapping("/architecture")
    public Result<Map<String, Object>> architecture() {
        Map<String, Object> architecture = new LinkedHashMap<>();
        architecture.put("version", "v2-monolith-plus");
        architecture.put("backend", "Spring Boot 3 monolith");
        architecture.put("frontend", "Vue3 + Vite + Element Plus");
        architecture.put("aiService", properties.aiServiceUrl());
        architecture.put("vectorStore", "Qdrant");
        architecture.put("search", "Elasticsearch keyword retrieval + Qdrant vector retrieval");
        architecture.put("observability", "Spring Boot Actuator + Prometheus + Grafana");
        architecture.put("resilience", "Sentinel flow control and local fallback");
        architecture.put("cache", "Caffeine local cache + Redis shared cache");
        architecture.put("onlinePath", List.of("Caffeine hot data", "Redis cache", "Redis Lua budget deduction", "RabbitMQ async logs"));
        architecture.put("queues", List.of(
                        RabbitMqConfig.AD_EVENT_QUEUE,
                        RabbitMqConfig.BUDGET_TRANSACTION_QUEUE,
                        RabbitMqConfig.CREATIVE_AUDIT_QUEUE,
                        RabbitMqConfig.KNOWLEDGE_INDEX_QUEUE,
                        RabbitMqConfig.DASHBOARD_AGGREGATE_QUEUE
        ));
        return Result.ok(architecture);
    }

    /**
     * 组件配置快照。
     * 这里展示客户端和外部地址，不做真实连通性检测；真实探活后续可放到 actuator/observability 模块。
     */
    @GetMapping("/components")
    public Result<Map<String, Object>> components() {
        return Result.ok(Map.of(
                "redisTemplate", redisTemplate.getClass().getSimpleName(),
                "rabbitTemplate", rabbitTemplate.getClass().getSimpleName(),
                "qdrantUrl", properties.qdrantUrl(),
                "minioEndpoint", properties.minioEndpoint(),
                "elasticsearchUrl", properties.elasticsearchUrl()
        ));
    }
}
