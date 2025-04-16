package com.caloriecompass.infrastructure.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class FoodModels {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodSearchResponse {
        @JsonProperty("foods")
        private Foods foods;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Foods {
        @JsonProperty("food")
        private List<Food> food;

        @JsonProperty("max_results")
        private Integer maxResults;

        @JsonProperty("page_number")
        private Integer pageNumber;

        @JsonProperty("total_results")
        private Integer totalResults;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Food {
        @JsonProperty("food_id")
        private String foodId;

        @JsonProperty("food_name")
        private String foodName;

        @JsonProperty("food_type")
        private String foodType;

        @JsonProperty("food_url")
        private String foodUrl;

        @JsonProperty("brand_name")
        private String brandName;

        @JsonProperty("food_description")
        private String foodDescription;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodGetResponse {
        @JsonProperty("food")
        private FoodDetails food;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodDetails {
        @JsonProperty("food_id")
        private String foodId;

        @JsonProperty("food_name")
        private String foodName;

        @JsonProperty("brand_name")
        private String brandName;

        @JsonProperty("servings")
        private Servings servings;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Servings {
        @JsonProperty("serving")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Serving> serving;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Serving {
        @JsonProperty("serving_id")
        private String servingId;

        @JsonProperty("serving_description")
        private String servingDescription;

        @JsonProperty("serving_url")
        private String servingUrl;

        @JsonProperty("metric_serving_amount")
        private String metricServingAmount;

        @JsonProperty("metric_serving_unit")
        private String metricServingUnit;

        @JsonProperty("calories")
        private String calories;

        @JsonProperty("carbohydrate")
        private String carbohydrate;

        @JsonProperty("protein")
        private String protein;

        @JsonProperty("fat")
        private String fat;

        @JsonProperty("saturated_fat")
        private String saturatedFat;

        @JsonProperty("polyunsaturated_fat")
        private String polyunsaturatedFat;

        @JsonProperty("monounsaturated_fat")
        private String monounsaturatedFat;

        @JsonProperty("trans_fat")
        private String transFat;

        @JsonProperty("cholesterol")
        private String cholesterol;

        @JsonProperty("sodium")
        private String sodium;

        @JsonProperty("potassium")
        private String potassium;

        @JsonProperty("fiber")
        private String fiber;

        @JsonProperty("sugar")
        private String sugar;

        @JsonProperty("vitamin_a")
        private String vitaminA;

        @JsonProperty("vitamin_c")
        private String vitaminC;

        @JsonProperty("calcium")
        private String calcium;

        @JsonProperty("iron")
        private String iron;
    }
}