package com.resourceSharingPlatform.AI_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableMongoAuditing
public class AppConfig {

    @Value("${resource.management.service.url}")
    private String resourceServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(resourceServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}