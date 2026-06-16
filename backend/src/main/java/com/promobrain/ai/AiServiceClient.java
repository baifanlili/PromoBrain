package com.promobrain.ai;

import com.promobrain.common.config.PromoBrainProperties;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final PromoBrainProperties properties;

    public AiServiceClient(RestClient restClient, PromoBrainProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    /**
     * 调用 AI 服务生成广告素材。
     * 业务侧只依赖这个方法，不直接关心 Python 服务和模型供应商细节。
     */
    public Map<String, Object> generateCreative(Map<String, Object> request) {
        return post("/ai/creative/generate", request);
    }

    /**
     * 调用 AI 服务推荐关键词。
     * 后续接入多路召回和 rerank 后，后端接口形状保持不变。
     */
    public Map<String, Object> recommendKeyword(Map<String, Object> request) {
        return post("/ai/keyword/recommend", request);
    }

    /**
     * 调用 AI 服务检索知识库。
     * 第一版可以 mock，后续替换为 Qdrant 真实向量检索。
     */
    public Map<String, Object> searchKnowledge(Map<String, Object> request) {
        return post("/ai/retrieval/search", request);
    }

    /**
     * 统一封装 AI HTTP 调用和降级。
     * 模型服务在本地开发中经常不可用，后端必须返回可解释的降级结果，不能阻塞广告业务闭环。
     */
    private Map<String, Object> post(String path, Map<String, Object> request) {
        try {
            return restClient.post()
                    .uri(properties.aiServiceUrl() + path)
                    .body(request)
                    .retrieve()
                    .body(MAP_RESPONSE_TYPE);
        } catch (RuntimeException ex) {
            return Map.of(
                    "mock", true,
                    "degraded", true,
                    "reason", ex.getClass().getSimpleName(),
                    "items", List.of()
            );
        }
    }
}
