package com.promobrain.search;

import com.promobrain.common.response.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第二版混合检索接口。
 * 对前端暴露统一 search API，内部可以逐步扩展 Elasticsearch、Qdrant 和 rerank。
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final HybridSearchService hybridSearchService;

    /**
     * 注入第二版混合检索服务。
     * Controller 只负责协议适配，合并召回和降级策略都放在 Service，便于后续接真实索引。
     */
    public SearchController(HybridSearchService hybridSearchService) {
        this.hybridSearchService = hybridSearchService;
    }

    /**
     * 执行关键词 + 向量混合检索。
     * 当前用于验证第二版搜索架构入口，后续接入真实索引数据。
     */
    @PostMapping("/hybrid")
    public Result<HybridSearchService.HybridSearchResult> hybrid(@Valid @RequestBody HybridSearchRequest request) {
        return Result.ok(hybridSearchService.search(request.merchantId(), request.productId(), request.query()));
    }

    /**
     * 混合检索请求。
     */
    public record HybridSearchRequest(
            @NotNull Long merchantId,
            @NotNull Long productId,
            @NotBlank String query
    ) {
    }
}
