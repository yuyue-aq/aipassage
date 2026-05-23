package com.yuyue.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Pexels 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "pexels")
public class PexelsConfig {

    /**
     * Pexels API Key
     */
    private String apiKey;
}

