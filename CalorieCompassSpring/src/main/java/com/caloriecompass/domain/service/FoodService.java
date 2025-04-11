package com.caloriecompass.domain.service;

import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.NutritionInfo;

import java.util.List;

public interface FoodService {
    /**
     * Search for foods by query string
     * 
     * @param query Search query
     * @param page  Page number (starting from 0)
     * @param size  Results per page
     * @return List of foods matching the search
     */
    List<Food> searchFoods(String query, int page, int size);

    /**
     * Get detailed food information by ID
     * 
     * @param foodId Food ID
     * @return Food details
     */
    Food getFoodById(String foodId);

    /**
     * Get nutritional information for a specific food
     * 
     * @param foodId Food ID
     * @return List of nutrition information for different servings
     */
    List<NutritionInfo> getNutritionInfo(String foodId);
}