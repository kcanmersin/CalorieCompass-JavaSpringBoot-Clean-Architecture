package com.caloriecompass.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FatSecretConfig {
    @Value("${fatsecret.client-id}")
    private String clientId;

    @Value("${fatsecret.client-secret}")
    private String clientSecret;

    @Value("${fatsecret.scope}")
    private String scope;

    @Value("${fatsecret.auth-endpoint}")
    private String authEndpoint;

    @Value("${fatsecret.api-endpoint}")
    private String apiEndpoint;
}