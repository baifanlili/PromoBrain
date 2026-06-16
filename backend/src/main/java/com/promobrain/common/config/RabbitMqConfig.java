package com.promobrain.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // v1 keeps the topology deliberately small: one durable direct exchange
    // with queues split by business concern. Consumers can evolve independently
    // without changing the synchronous API path.
    public static final String EXCHANGE = "promobrain.exchange";

    public static final String AD_EVENT_QUEUE = "ad.event.queue";
    public static final String BUDGET_TRANSACTION_QUEUE = "budget.transaction.queue";
    public static final String CREATIVE_AUDIT_QUEUE = "creative.audit.queue";
    public static final String KNOWLEDGE_INDEX_QUEUE = "knowledge.index.queue";
    public static final String DASHBOARD_AGGREGATE_QUEUE = "dashboard.aggregate.queue";

    /**
     * 声明第一版统一交换机。
     * 使用 direct exchange 是为了让不同业务消息按 routing key 明确进入对应队列。
     */
    @Bean
    DirectExchange promoBrainExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    /**
     * 广告曝光、点击、转化事件队列。
     */
    @Bean
    Queue adEventQueue() {
        return new Queue(AD_EVENT_QUEUE, true);
    }

    /**
     * 预算流水落库队列。
     */
    @Bean
    Queue budgetTransactionQueue() {
        return new Queue(BUDGET_TRANSACTION_QUEUE, true);
    }

    /**
     * 素材审核任务队列。
     */
    @Bean
    Queue creativeAuditQueue() {
        return new Queue(CREATIVE_AUDIT_QUEUE, true);
    }

    /**
     * 知识库索引任务队列。
     */
    @Bean
    Queue knowledgeIndexQueue() {
        return new Queue(KNOWLEDGE_INDEX_QUEUE, true);
    }

    /**
     * 看板聚合任务队列。
     */
    @Bean
    Queue dashboardAggregateQueue() {
        return new Queue(DASHBOARD_AGGREGATE_QUEUE, true);
    }

    /**
     * 绑定广告事件 routing key。
     */
    @Bean
    Binding adEventBinding(
            @Qualifier("adEventQueue") Queue adEventQueue,
            DirectExchange promoBrainExchange
    ) {
        return BindingBuilder.bind(adEventQueue).to(promoBrainExchange).with("ad.event");
    }

    /**
     * 绑定预算流水 routing key。
     */
    @Bean
    Binding budgetTransactionBinding(
            @Qualifier("budgetTransactionQueue") Queue budgetTransactionQueue,
            DirectExchange promoBrainExchange
    ) {
        return BindingBuilder.bind(budgetTransactionQueue).to(promoBrainExchange).with("budget.transaction");
    }

    /**
     * 绑定素材审核 routing key。
     */
    @Bean
    Binding creativeAuditBinding(
            @Qualifier("creativeAuditQueue") Queue creativeAuditQueue,
            DirectExchange promoBrainExchange
    ) {
        return BindingBuilder.bind(creativeAuditQueue).to(promoBrainExchange).with("creative.audit");
    }

    /**
     * 绑定知识库索引 routing key。
     */
    @Bean
    Binding knowledgeIndexBinding(
            @Qualifier("knowledgeIndexQueue") Queue knowledgeIndexQueue,
            DirectExchange promoBrainExchange
    ) {
        return BindingBuilder.bind(knowledgeIndexQueue).to(promoBrainExchange).with("knowledge.index");
    }

    /**
     * 绑定看板聚合 routing key。
     */
    @Bean
    Binding dashboardAggregateBinding(
            @Qualifier("dashboardAggregateQueue") Queue dashboardAggregateQueue,
            DirectExchange promoBrainExchange
    ) {
        return BindingBuilder.bind(dashboardAggregateQueue).to(promoBrainExchange).with("dashboard.aggregate");
    }
}
