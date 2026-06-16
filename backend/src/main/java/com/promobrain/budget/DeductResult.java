package com.promobrain.budget;

import java.math.BigDecimal;

/**
 * 预算扣减结果。
 * 统一把 Redis Lua 返回码转换为前端和日志更容易理解的业务状态。
 */
public record DeductResult(boolean success, String code, BigDecimal remainingBudget) {

    /**
     * 将 Lua 脚本返回码翻译成业务语义。
     * 这里必须和 `lua/budget_deduct.lua` 的返回码保持一致，修改脚本时要同步修改本方法。
     */
    public static DeductResult fromCode(long code, BigDecimal remainingBudget) {
        return switch ((int) code) {
            case 1 -> new DeductResult(true, "DEDUCT_SUCCESS", remainingBudget);
            case 0 -> new DeductResult(false, "BUDGET_NOT_ENOUGH", remainingBudget);
            case 2 -> new DeductResult(false, "DUPLICATE_REQUEST", remainingBudget);
            case -1 -> new DeductResult(false, "BUDGET_NOT_FOUND", remainingBudget);
            default -> new DeductResult(false, "DEDUCT_UNKNOWN", remainingBudget);
        };
    }
}
