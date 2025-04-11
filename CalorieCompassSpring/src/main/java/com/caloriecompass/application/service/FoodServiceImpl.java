package com.caloriecompass.application.service;

import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.NutritionInfo;
import com.caloriecompass.domain.service.FoodService;
import com.caloriecompass.infrastructure.api.model.FoodModels;
import com.caloriecompass.infrastructure.api.service.FatSecretApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FatSecretApiService fatSecretApiService;

    @Override
    public List<Food> searchFoods(String query, int page, int size) {
        try {
            FoodModels.FoodSearchResponse response = fatSecretApiService.searchFoods(query, page + 1, size);

            if (response == null || response.getFoods() == null || response.getFoods().getFood() == null) {
                return Collections.emptyList();
            }

            return response.getFoods().getFood().stream()
                    .map(this::mapToFood)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching foods", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Food getFoodById(String foodId) {
        try {
            FoodModels.FoodGetResponse response = fatSecretApiService.getFood(foodId);

            if (response == null || response.getFood() == null) {
                return null;
            }

            FoodModels.FoodDetails foodDetails = response.getFood();

            return Food.builder()
                    .id(foodDetails.getFoodId())
                    .name(foodDetails.getFoodName())
                    .brand(foodDetails.getBrandName())
                    .build();
        } catch (Exception e) {
            log.error("Error getting food by ID", e);
            return null;
        }
    }

    @Override
    public List<NutritionInfo> getNutritionInfo(String foodId) {
        try {
            FoodModels.FoodGetResponse response = fatSecretApiService.getFood(foodId);

            if (response == null || response.getFood() == null ||
                    response.getFood().getServings() == null ||
                    response.getFood().getServings().getServing() == null) {
                return Collections.emptyList();
            }

            List<FoodModels.Serving> servings = response.getFood().getServings().getServing();

            return servings.stream()
                    .map(this::mapToNutritionInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting nutrition info", e);
            return Collections.emptyList();
        }
    }

    private Food mapToFood(FoodModels.Food foodModel) {
        return Food.builder()
                .id(foodModel.getFoodId())
                .name(foodModel.getFoodName())
                .brand(foodModel.getBrandName())
                .description(foodModel.getFoodDescription())
                .foodType(foodModel.getFoodType())
                .build();
    }

    private NutritionInfo mapToNutritionInfo(FoodModels.Serving serving) {
        return NutritionInfo.builder()
                .servingId(serving.getServingId())
                .servingDescription(serving.getServingDescription())
                .servingAmount(parseDecimal(serving.getMetricServingAmount()))
                .servingUnit(serving.getMetricServingUnit())
                .calories(parseDecimal(serving.getCalories()))
                .carbohydrates(parseDecimal(serving.getCarbohydrate()))
                .protein(parseDecimal(serving.getProtein()))
                .fat(parseDecimal(serving.getFat()))
                .saturatedFat(parseDecimal(serving.getSaturatedFat()))
                .polyunsaturatedFat(parseDecimal(serving.getPolyunsaturatedFat()))
                .monounsaturatedFat(parseDecimal(serving.getMonounsaturatedFat()))
                .transFat(parseDecimal(serving.getTransFat()))
                .cholesterol(parseDecimal(serving.getCholesterol()))
                .sodium(parseDecimal(serving.getSodium()))
                .potassium(parseDecimal(serving.getPotassium()))
                .fiber(parseDecimal(serving.getFiber()))
                .sugar(parseDecimal(serving.getSugar()))
                .vitaminA(parseDecimal(serving.getVitaminA()))
                .vitaminC(parseDecimal(serving.getVitaminC()))
                .calcium(parseDecimal(serving.getCalcium()))
                .iron(parseDecimal(serving.getIron()))
                .build();
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}