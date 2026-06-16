package com.promobrain.common.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.context.annotation.Configuration;

/**
 * 第二版 Sentinel 限流配置。
 * 当前先使用本地静态规则，后续接入 Dashboard 或 Nacos 时只替换规则来源。
 */
@Configuration
public class SentinelConfig {

    public static final String AD_SERVING_RESOURCE = "ad-serving-request";
    public static final String AI_GENERATE_RESOURCE = "ai-creative-generate";

    /**
     * 初始化核心资源限流规则。
     * 广告请求和 AI 生成是最容易被压测打穿的入口，先给它们建立明确的保护点。
     */
    @PostConstruct
    public void initializeFlowRules() {
        FlowRule servingRule = new FlowRule(AD_SERVING_RESOURCE);
        servingRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        servingRule.setCount(100);

        FlowRule aiRule = new FlowRule(AI_GENERATE_RESOURCE);
        aiRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        aiRule.setCount(20);

        FlowRuleManager.loadRules(List.of(servingRule, aiRule));
    }
}

