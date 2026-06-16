package com.promobrain.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PromoBrain 自定义配置项。
 * 将 AI 服务、Qdrant、MinIO 等外部依赖集中管理，避免业务代码散落硬编码地址。
 */
@ConfigurationProperties(prefix = "promobrain")
public record PromoBrainProperties(
        String aiServiceUrl,
        String qdrantUrl,
        String minioEndpoint,
        String elasticsearchUrl
) {
}
