package com.promobrain.budget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class BudgetDeductService {

    // Click idempotency only needs to cover the time window where clients may retry.
    // The database unique index remains the final guard for durable deduplication.
    private static final Duration IDEMPOTENT_TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> budgetDeductScript;

    public BudgetDeductService(StringRedisTemplate redisTemplate, DefaultRedisScript<Long> budgetDeductScript) {
        this.redisTemplate = redisTemplate;
        this.budgetDeductScript = budgetDeductScript;
    }

    /**
     * 执行点击扣费。
     * 预算判断、幂等判断和扣减必须保持在一次 Redis Lua 调用里，避免并发点击导致超扣。
     */
    public DeductResult deduct(Long campaignId, String requestId, BigDecimal cost) {
        String budgetKey = "ad:budget:remaining:" + campaignId;
        String requestKey = "ad:click:deduct:" + requestId;

        Long code = redisTemplate.execute(
                budgetDeductScript,
                List.of(budgetKey, requestKey),
                cost.toPlainString(),
                String.valueOf(IDEMPOTENT_TTL.toSeconds())
        );

        BigDecimal remaining = readRemainingBudget(budgetKey);
        return DeductResult.fromCode(code == null ? -99 : code, remaining);
    }

    /**
     * 初始化广告计划预算。
     * 第一版用于本地演示；后续应由广告计划发布流程写入预算缓存。
     */
    public void initializeBudget(Long campaignId, BigDecimal amount) {
        redisTemplate.opsForValue().set("ad:budget:remaining:" + campaignId, amount.toPlainString());
    }

    /**
     * 读取 Redis 中的剩余预算。
     * 扣减失败时也返回当前余额，方便前端展示预算不足或重复请求的状态。
     */
    private BigDecimal readRemainingBudget(String budgetKey) {
        String value = redisTemplate.opsForValue().get(budgetKey);
        return value == null ? null : new BigDecimal(value).setScale(4, RoundingMode.HALF_UP);
    }
}
