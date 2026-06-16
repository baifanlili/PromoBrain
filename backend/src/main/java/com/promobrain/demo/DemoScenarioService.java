package com.promobrain.demo;

import com.promobrain.ai.AiServiceClient;
import com.promobrain.common.config.RabbitMqConfig;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DemoScenarioService {

    private final AiServiceClient aiServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public DemoScenarioService(AiServiceClient aiServiceClient, RabbitTemplate rabbitTemplate) {
        this.aiServiceClient = aiServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 组装第一版演示快照。
     * 目前使用稳定 mock 数据，目的是先固定前后端契约和展示链路。
     */
    public DemoSnapshot snapshot() {
        ProductSummary product = demoProduct();
        CampaignSummary campaign = demoCampaign();

        return new DemoSnapshot(
                product,
                campaign,
                List.of("冰丝防晒衣", "UPF50+", "通勤防晒", "骑行防晒"),
                new DashboardSummary(12800, 832, 0.065, 0.042, new BigDecimal("665.60"), new BigDecimal("4386.00"), 6.59),
                List.of(
                        "Spring Boot 单体承载核心广告业务",
                        "Redis Lua 保证点击扣费原子性",
                        "RabbitMQ 异步写曝光、点击和预算流水",
                        "FastAPI + Qdrant 承接 RAG 与 Agent 能力",
                        "Vue3 管理台展示完整业务闭环"
                )
        );
    }

    /**
     * 调用 AI 服务生成素材。
     * 后续真实实现会先召回商品知识、历史文案和广告规则，再调用模型生成。
     */
    public Map<String, Object> generateCreative(Long productId) {
        return aiServiceClient.generateCreative(Map.of(
                "productId", productId,
                "platform", "PDD",
                "style", "CONVERSION",
                "count", 3,
                "requirement", "突出冰丝凉感和通勤防晒，避免夸大效果"
        ));
    }

    /**
     * 调用 AI 服务推荐关键词。
     * 第一版保证接口可用，后续会接入多路召回、去重、禁用词过滤和排序。
     */
    public Map<String, Object> recommendKeyword(Long productId) {
        return aiServiceClient.recommendKeyword(Map.of(
                "productId", productId,
                "campaignGoal", "CONVERSION",
                "topK", 10
        ));
    }

    /**
     * 模拟广告在线请求。
     * 主链路返回广告结果，同时把曝光日志写入 RabbitMQ；MQ 不可用时降级但不影响返回广告。
     */
    public ServingResult serving(String requestId, String keyword) {
        CampaignSummary campaign = demoCampaign();
        ProductSummary product = demoProduct();
        AdCandidate ad = new AdCandidate(
                campaign.id(),
                product.id(),
                5001L,
                "夏天通勤不闷热，这件防晒衣真的轻",
                "UPF50+ 面料，轻薄透气，通勤、骑行、户外都能穿。",
                0.91
        );

        boolean mqSent = publishAdEvent(Map.of(
                "requestId", requestId,
                "campaignId", campaign.id(),
                "productId", product.id(),
                "creativeId", ad.creativeId(),
                "keyword", keyword,
                "eventType", "IMPRESSION",
                "createdAt", LocalDateTime.now().toString()
        ));

        return new ServingResult(List.of(ad), mqSent);
    }

    /**
     * 发布广告事件消息。
     * 异步日志是广告链路降延迟的关键点，因此失败时只返回状态，不抛出阻塞主流程。
     */
    private boolean publishAdEvent(Map<String, Object> message) {
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, "ad.event", message);
            return true;
        } catch (AmqpException ex) {
            // RabbitMQ 在本地开发时可能尚未启动。广告请求主链路不能因为
            // 异步日志组件不可用而整体失败，因此这里显式降级并把状态返回给前端。
            return false;
        }
    }

    /**
     * 演示商品数据。
     * 后续替换为 product 表查询时保持 ProductSummary 结构不变。
     */
    private ProductSummary demoProduct() {
        return new ProductSummary(
                101L,
                "夏季轻薄防晒衣",
                "服饰",
                new BigDecimal("129.00"),
                List.of("UPF50+", "冰丝凉感", "轻薄透气"),
                List.of("户外通勤女性", "学生", "骑行人群")
        );
    }

    /**
     * 演示广告计划数据。
     * 后续替换为 ad_campaign 表查询和 Redis 缓存读取。
     */
    private CampaignSummary demoCampaign() {
        return new CampaignSummary(
                1001L,
                "夏季防晒衣搜索推广",
                "CONVERSION",
                new BigDecimal("5000.00"),
                "PUBLISHED"
        );
    }

    /**
     * 前端首页展示所需的完整演示快照。
     */
    public record DemoSnapshot(
            ProductSummary product,
            CampaignSummary campaign,
            List<String> keywords,
            DashboardSummary dashboard,
            List<String> architectureHighlights
    ) {
    }

    /**
     * 商品摘要，避免第一版前端直接依赖完整商品表字段。
     */
    public record ProductSummary(
            Long id,
            String productName,
            String category,
            BigDecimal price,
            List<String> sellingPoints,
            List<String> targetUsers
    ) {
    }

    /**
     * 广告计划摘要。
     */
    public record CampaignSummary(
            Long id,
            String campaignName,
            String goal,
            BigDecimal totalBudget,
            String status
    ) {
    }

    /**
     * 看板摘要指标。
     */
    public record DashboardSummary(
            long impressions,
            long clicks,
            double ctr,
            double cvr,
            BigDecimal cost,
            BigDecimal gmv,
            double roi
    ) {
    }

    /**
     * 广告候选结果。
     */
    public record AdCandidate(
            Long campaignId,
            Long productId,
            Long creativeId,
            String title,
            String content,
            double rankScore
    ) {
    }

    /**
     * 广告请求结果，mqSent 用于告诉前端曝光日志是否进入异步队列。
     */
    public record ServingResult(List<AdCandidate> ads, boolean mqSent) {
    }
}
