package com.caloriecompass.infrastructure.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuthToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    // Calculate expiration timestamp when token is received
    private long expirationTimestamp;

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        // Set expiration time in milliseconds
        this.expirationTimestamp = System.currentTimeMillis() + (expiresIn * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTimestamp;
    }
}