package com.promobrain.search;

import com.promobrain.ai.AiServiceClient;
import com.promobrain.common.config.PromoBrainProperties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * 第二版混合检索服务。
 * 关键词检索由 Elasticsearch 承接，语义检索由 Qdrant/FastAPI 承接，本服务负责合并和重排。
 */
@Service
public class HybridSearchService {

    private final RestClient restClient;
    private final PromoBrainProperties properties;
    private final AiServiceClient aiServiceClient;

    /**
     * 注入检索链路依赖。
     * RestClient 用于探测 Elasticsearch，AI 客户端继续复用第一版 FastAPI/Qdrant 入口，避免后端直接绑定向量库细节。
     */
    public HybridSearchService(RestClient restClient, PromoBrainProperties properties, AiServiceClient aiServiceClient) {
        this.restClient = restClient;
        this.properties = properties;
        this.aiServiceClient = aiServiceClient;
    }

    /**
     * 执行混合检索。
     * 第一版先返回稳定 mock 和降级结构；Elasticsearch 不可用时不影响 Qdrant mock 语义召回。
     */
    @Cacheable(cacheNames = "hybridSearch", key = "#merchantId + ':' + #productId + ':' + #query")
    public HybridSearchResult search(Long merchantId, Long productId, String query) {
        List<SearchItem> items = new ArrayList<>();
        items.addAll(searchByElasticsearch(query));
        items.addAll(searchByVector(merchantId, productId, query));

        List<SearchItem> ranked = items.stream()
                .sorted(Comparator.comparingDouble(SearchItem::score).reversed())
                .limit(10)
                .toList();

        return new HybridSearchResult(query, properties.elasticsearchUrl(), properties.qdrantUrl(), ranked);
    }

    /**
     * Elasticsearch 关键词检索。
     * 当前只做健康探测和 mock 结果，后续接入索引后替换为真实 DSL 查询。
     */
    private List<SearchItem> searchByElasticsearch(String query) {
        boolean available = isElasticsearchAvailable();
        return List.of(new SearchItem(
                "es_demo_001",
                "KEYWORD",
                "Elasticsearch 召回与“" + query + "”相关的广告规则和商品卖点",
                available ? 0.88 : 0.68,
                available ? "elasticsearch" : "elasticsearch-degraded"
        ));
    }

    /**
     * Qdrant 语义检索。
     * 通过 AI 服务访问向量能力，避免 Java 后端直接绑定向量库实现细节。
     */
    private List<SearchItem> searchByVector(Long merchantId, Long productId, String query) {
        Map<String, Object> response = aiServiceClient.searchKnowledge(Map.of(
                "merchantId", merchantId,
                "productId", productId,
                "query", query,
                "topK", 5
        ));
        return List.of(new SearchItem(
                "vector_demo_001",
                "VECTOR",
                String.valueOf(response.getOrDefault("results", "Qdrant 语义召回 mock 结果")),
                0.91,
                "qdrant"
        ));
    }

    /**
     * 判断 Elasticsearch 是否可用。
     * 失败时只影响关键词召回分支，不阻断整体混合检索。
     */
    private boolean isElasticsearchAvailable() {
        try {
            restClient.get()
                    .uri(properties.elasticsearchUrl())
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * 混合检索响应结构。
     */
    public record HybridSearchResult(String query, String elasticsearchUrl, String qdrantUrl, List<SearchItem> items) {
    }

    /**
     * 检索候选项。
     */
    public record SearchItem(String id, String sourceType, String content, double score, String sourceStatus) {
    }
}
