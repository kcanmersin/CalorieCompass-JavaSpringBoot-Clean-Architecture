package com.caloriecompass.infrastructure.api.service;

import com.caloriecompass.config.FatSecretConfig;
import com.caloriecompass.infrastructure.api.model.FoodModels.FoodGetResponse;
import com.caloriecompass.infrastructure.api.model.FoodModels.FoodSearchResponse;
import com.caloriecompass.infrastructure.api.model.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class FatSecretApiService {

    private final FatSecretConfig fatSecretConfig;
    private final RestTemplate restTemplate;

    private OAuthToken oAuthToken;
    private final ReentrantLock tokenLock = new ReentrantLock();

    public OAuthToken getToken() {
        tokenLock.lock();
        try {
            if (oAuthToken == null || oAuthToken.isExpired()) {
                log.info("Obtaining new FatSecret API token");
                oAuthToken = fetchNewToken();
            }
            return oAuthToken;
        } finally {
            tokenLock.unlock();
        }
    }

    private OAuthToken fetchNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = fatSecretConfig.getClientId() + ":" + fatSecretConfig.getClientSecret();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        headers.set("Authorization", "Basic " + new String(encodedAuth));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", fatSecretConfig.getScope());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<OAuthToken> response = restTemplate.exchange(
                    fatSecretConfig.getAuthEndpoint(),
                    HttpMethod.POST,
                    entity,
                    OAuthToken.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Failed to obtain token, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to obtain FatSecret API token");
            }
        } catch (Exception e) {
            log.error("Error obtaining FatSecret API token", e);
            throw new RuntimeException("Error obtaining FatSecret API token", e);
        }
    }

    public FoodSearchResponse searchFoods(String query, int pageNumber, int maxResults) {
        OAuthToken token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Build query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fatSecretConfig.getApiEndpoint())
                .queryParam("method", "foods.search")
                .queryParam("search_expression", query)
                .queryParam("page_number", pageNumber)
                .queryParam("max_results", maxResults)
                .queryParam("format", "json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<FoodSearchResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    FoodSearchResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Failed to search foods, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to search foods");
            }
        } catch (Exception e) {
            log.error("Error searching foods", e);
            throw new RuntimeException("Error searching foods", e);
        }
    }

    public FoodGetResponse getFood(String foodId) {
        OAuthToken token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fatSecretConfig.getApiEndpoint())
                .queryParam("method", "food.get")
                .queryParam("food_id", foodId)
                .queryParam("format", "json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<FoodGetResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    FoodGetResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Failed to get food details, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get food details");
            }
        } catch (Exception e) {
            log.error("Error getting food details", e);
            throw new RuntimeException("Error getting food details", e);
        }
    }
}