package com.promobrain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    /**
     * 后端访问 AI 服务的 HTTP 客户端。
     * 第一版使用 Spring RestClient，后续需要统一超时、重试、观测指标时从这里扩展。
     */
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(5000);
        return builder.requestFactory(requestFactory).build();
    }
}
