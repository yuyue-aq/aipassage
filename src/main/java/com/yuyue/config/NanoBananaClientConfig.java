package com.yuyue.config;

import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.genai.types.ProxyOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * Nano Banana (Gemini) Client 配置
 * 创建线程安全的共享 GenAI Client，支持 HTTP 代理
 *
 */
@Configuration
@Slf4j
public class NanoBananaClientConfig {

    /**
     * 创建共享的 GenAI Client Bean（线程安全，可复用）
     * 仅在配置了 api-key 时创建
     */
    @Bean
    @ConditionalOnProperty(name = "nano-banana.api-key")
    public Client nanoBananaClient(NanoBananaConfig config) {
        Client.Builder builder = Client.builder()
                .apiKey(config.getApiKey());

        // 如果配置了代理，通过 ClientOptions 设置
        String proxyHost = config.getProxyHost();
        Integer proxyPort = config.getProxyPort();
        if ((proxyHost == null || proxyHost.isBlank() || proxyPort == null || proxyPort <= 0)) {
            ProxyHostPort envProxy = resolveProxyFromEnvOrSystem();
            if (envProxy != null) {
                proxyHost = envProxy.host();
                proxyPort = envProxy.port();
            }
        }

        if (proxyHost != null && !proxyHost.isBlank() && proxyPort != null && proxyPort > 0) {
            ClientOptions clientOptions = ClientOptions.builder()
                    .proxyOptions(ProxyOptions.builder()
                            .host(proxyHost)
                            .port(proxyPort)
                            .type("HTTP")
                            .build())
                    .build();
            builder.clientOptions(clientOptions);
            log.info("Nano Banana Client 已配置代理: {}:{}", proxyHost, proxyPort);
        }

        Client client = builder.build();
        log.info("Nano Banana Client 已创建, model={}", config.getModel());
        return client;
    }

    private ProxyHostPort resolveProxyFromEnvOrSystem() {
        String proxyUrl = System.getenv("HTTPS_PROXY");
        if (proxyUrl == null || proxyUrl.isBlank()) {
            proxyUrl = System.getenv("HTTP_PROXY");
        }
        if (proxyUrl != null && !proxyUrl.isBlank()) {
            ProxyHostPort parsed = parseProxyUrl(proxyUrl);
            if (parsed != null) {
                return parsed;
            }
        }

        String sysHost = System.getProperty("https.proxyHost");
        String sysPort = System.getProperty("https.proxyPort");
        if ((sysHost == null || sysHost.isBlank())) {
            sysHost = System.getProperty("http.proxyHost");
            sysPort = System.getProperty("http.proxyPort");
        }
        if (sysHost != null && !sysHost.isBlank() && sysPort != null && !sysPort.isBlank()) {
            try {
                int port = Integer.parseInt(sysPort);
                if (port > 0) {
                    return new ProxyHostPort(sysHost, port);
                }
            } catch (NumberFormatException ignored) {
                // ignore invalid system proxy port
            }
        }
        return null;
    }

    private ProxyHostPort parseProxyUrl(String proxyUrl) {
        try {
            URI uri = new URI(proxyUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            if (host != null && !host.isBlank() && port > 0) {
                return new ProxyHostPort(host, port);
            }
        } catch (Exception ignored) {
            // ignore invalid proxy url
        }
        return null;
    }

    private record ProxyHostPort(String host, int port) {}
}
