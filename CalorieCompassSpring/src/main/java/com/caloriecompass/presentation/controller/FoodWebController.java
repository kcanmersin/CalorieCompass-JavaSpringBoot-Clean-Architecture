package com.caloriecompass.presentation.controller;

import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.NutritionInfo;
import com.caloriecompass.domain.service.FoodService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/foods")
@RequiredArgsConstructor
@Tag(name = "Food Web", description = "Food search and nutrition web interface")
public class FoodWebController {

    private final FoodService foodService;

    @GetMapping("/search")
    @Operation(summary = "Search foods", description = "Search for foods by query term and display results in web interface", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search page displayed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public String searchFoods(
            @Parameter(description = "Search query") @RequestParam(required = false) String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Model model,
            Authentication authentication) {

        if (query != null && !query.isEmpty()) {
            List<Food> foods = foodService.searchFoods(query, page, size);
            model.addAttribute("foods", foods);
            model.addAttribute("query", query);
        }

        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "food/search";
    }

    @GetMapping("/{foodId}")
    @Operation(summary = "Get food details", description = "Get food and nutrition details by ID and display in web interface", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food details page displayed"),
            @ApiResponse(responseCode = "302", description = "Food not found, redirected to search"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public String getFoodDetails(
            @Parameter(description = "Food ID") @PathVariable String foodId,
            Model model,
            Authentication authentication) {

        Food food = foodService.getFoodById(foodId);
        List<NutritionInfo> nutritionInfoList = foodService.getNutritionInfo(foodId);

        if (food == null) {
            return "redirect:/foods/search";
        }

        model.addAttribute("food", food);
        model.addAttribute("nutritionInfoList", nutritionInfoList);

        return "food/details";
    }
}