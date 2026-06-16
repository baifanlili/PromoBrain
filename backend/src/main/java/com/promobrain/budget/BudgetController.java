package com.promobrain.budget;

import com.promobrain.common.response.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetDeductService budgetDeductService;

    public BudgetController(BudgetDeductService budgetDeductService) {
        this.budgetDeductService = budgetDeductService;
    }

    /**
     * 初始化演示预算。
     * 第一版为便于本地验证 Redis Lua，开放该接口；真实生产流程应由广告计划发布时写入。
     */
    @PostMapping("/init")
    public Result<Void> initialize(@Valid @RequestBody InitBudgetRequest request) {
        budgetDeductService.initializeBudget(request.campaignId(), request.amount());
        return Result.ok();
    }

    /**
     * 点击扣费接口。
     * 直接走 Redis Lua，验证第一版高并发预算扣减的核心链路。
     */
    @PostMapping("/deduct")
    public Result<DeductResult> deduct(@Valid @RequestBody DeductBudgetRequest request) {
        return Result.ok(budgetDeductService.deduct(request.campaignId(), request.requestId(), request.cost()));
    }

    /**
     * 初始化预算请求。
     */
    public record InitBudgetRequest(
            @NotNull Long campaignId,
            @NotNull @DecimalMin("0.01") BigDecimal amount
    ) {
    }

    /**
     * 点击扣费请求。
     * requestId 是幂等关键字段，前端或上游服务必须保证每次点击事件唯一。
     */
    public record DeductBudgetRequest(
            @NotBlank String requestId,
            @NotNull Long campaignId,
            Long creativeId,
            @NotNull @DecimalMin("0.01") BigDecimal cost
    ) {
    }
}
