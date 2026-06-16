package com.promobrain.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 本地前后端联调跨域配置。
 * 第一版允许 Vite 开发服务器访问后端，生产环境后续应收敛为 Nginx 同域或白名单域名。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 注册本地开发跨域规则。
     * 只开放常见 localhost 端口，避免为了调试直接放开所有来源。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
