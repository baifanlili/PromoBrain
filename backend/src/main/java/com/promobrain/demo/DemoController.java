package com.promobrain.demo;

import com.promobrain.common.response.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoScenarioService demoScenarioService;

    public DemoController(DemoScenarioService demoScenarioService) {
        this.demoScenarioService = demoScenarioService;
    }

    /**
     * 获取第一版演示快照。
     * 该接口服务前端首页和 README 截图，后续接数据库后保持响应结构稳定。
     */
    @GetMapping("/snapshot")
    public Result<DemoScenarioService.DemoSnapshot> snapshot() {
        return Result.ok(demoScenarioService.snapshot());
    }

    /**
     * 演示广告素材生成。
     * 第一版通过后端代理 AI 服务，确保前端不直接依赖模型服务地址。
     */
    @PostMapping("/creative/generate")
    public Result<Map<String, Object>> generateCreative(@Valid @RequestBody ProductActionRequest request) {
        return Result.ok(demoScenarioService.generateCreative(request.productId()));
    }

    /**
     * 演示关键词推荐。
     * 后续可在 service 内扩展商品卖点、历史词、向量召回等多路召回逻辑。
     */
    @PostMapping("/keyword/recommend")
    public Result<Map<String, Object>> recommendKeyword(@Valid @RequestBody ProductActionRequest request) {
        return Result.ok(demoScenarioService.recommendKeyword(request.productId()));
    }

    /**
     * 演示广告请求。
     * 第一版返回固定候选广告，并尝试发送 RabbitMQ 曝光日志。
     */
    @PostMapping("/serving/request")
    public Result<DemoScenarioService.ServingResult> serving(@Valid @RequestBody ServingRequest request) {
        return Result.ok(demoScenarioService.serving(request.requestId(), request.keyword()));
    }

    /**
     * 商品类演示动作的通用请求。
     */
    public record ProductActionRequest(@NotNull Long productId) {
    }

    /**
     * 广告请求模拟入参。
     */
    public record ServingRequest(
            @NotBlank String requestId,
            @NotBlank String keyword
    ) {
    }
}
