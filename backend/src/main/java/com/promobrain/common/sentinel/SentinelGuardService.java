package com.promobrain.common.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

/**
 * Sentinel 保护入口。
 * 业务代码通过这个服务包裹热点链路，避免散落 try/catch 和 Sentinel API 细节。
 */
@Service
public class SentinelGuardService {

    /**
     * 执行受 Sentinel 保护的代码块。
     * 触发限流时返回 fallback，保证第一版 demo 和第二版架构扩展都有可解释降级。
     */
    public <T> T guard(String resourceName, Supplier<T> supplier, Supplier<T> fallback) {
        try (Entry ignored = SphU.entry(resourceName)) {
            return supplier.get();
        } catch (BlockException ex) {
            return fallback.get();
        }
    }
}

