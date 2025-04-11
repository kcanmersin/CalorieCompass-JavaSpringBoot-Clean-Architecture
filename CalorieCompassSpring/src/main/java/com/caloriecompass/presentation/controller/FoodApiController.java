package com.caloriecompass.presentation.controller;

import com.caloriecompass.application.dto.response.FoodResponse;
import com.caloriecompass.application.dto.response.NutritionInfoResponse;
import com.caloriecompass.application.service.FoodMapperService;
import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.NutritionInfo;
import com.caloriecompass.domain.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
@Tag(name = "Food", description = "Food API for searching and nutrition information")
public class FoodApiController {

    private final FoodService foodService;
    private final FoodMapperService foodMapperService;

    @GetMapping("/search")
    @Operation(summary = "Search foods", description = "Search for foods by query term", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful", content = @Content(schema = @Schema(implementation = FoodResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FoodResponse>> searchFoods(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        List<Food> foods = foodService.searchFoods(query, page, size);
        List<FoodResponse> response = foodMapperService.toFoodResponseList(foods);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{foodId}")
    @Operation(summary = "Get food by ID", description = "Get food details by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food found"),
            @ApiResponse(responseCode = "404", description = "Food not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<FoodResponse> getFoodById(
            @Parameter(description = "Food ID") @PathVariable String foodId) {

        Food food = foodService.getFoodById(foodId);

        if (food == null) {
            return ResponseEntity.notFound().build();
        }

        FoodResponse response = foodMapperService.toFoodResponse(food);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{foodId}/nutrition")
    @Operation(summary = "Get nutrition information", description = "Get nutrition information for a food by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nutrition information found"),
            @ApiResponse(responseCode = "404", description = "Food not found or no nutrition information available"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<NutritionInfoResponse>> getNutritionInfo(
            @Parameter(description = "Food ID") @PathVariable String foodId) {

        List<NutritionInfo> nutritionInfo = foodService.getNutritionInfo(foodId);

        if (nutritionInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<NutritionInfoResponse> response = foodMapperService.toNutritionInfoResponseList(nutritionInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular foods", description = "Get a list of popular or commonly searched foods", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<FoodResponse>> getPopularFoods(
            @Parameter(description = "Max results to return") @RequestParam(defaultValue = "10") int limit) {

        List<Food> foods = foodService.searchFoods("apple", 0, limit);
        List<FoodResponse> response = foodMapperService.toFoodResponseList(foods);
        return ResponseEntity.ok(response);
    }
}