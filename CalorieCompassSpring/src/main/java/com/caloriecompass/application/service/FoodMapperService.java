package com.caloriecompass.application.service;

import com.caloriecompass.application.dto.response.FoodResponse;
import com.caloriecompass.application.dto.response.NutritionInfoResponse;
import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.NutritionInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodMapperService {

    public FoodResponse toFoodResponse(Food food) {
        if (food == null) {
            return null;
        }

        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .brand(food.getBrand())
                .description(food.getDescription())
                .foodType(food.getFoodType())
                .build();
    }

    public List<FoodResponse> toFoodResponseList(List<Food> foods) {
        if (foods == null) {
            return null;
        }

        return foods.stream()
                .map(this::toFoodResponse)
                .collect(Collectors.toList());
    }

    public NutritionInfoResponse toNutritionInfoResponse(NutritionInfo nutritionInfo) {
        if (nutritionInfo == null) {
            return null;
        }

        return NutritionInfoResponse.builder()
                .servingId(nutritionInfo.getServingId())
                .servingDescription(nutritionInfo.getServingDescription())
                .servingAmount(nutritionInfo.getServingAmount())
                .servingUnit(nutritionInfo.getServingUnit())
                .calories(nutritionInfo.getCalories())
                .carbohydrates(nutritionInfo.getCarbohydrates())
                .protein(nutritionInfo.getProtein())
                .fat(nutritionInfo.getFat())
                .saturatedFat(nutritionInfo.getSaturatedFat())
                .polyunsaturatedFat(nutritionInfo.getPolyunsaturatedFat())
                .monounsaturatedFat(nutritionInfo.getMonounsaturatedFat())
                .transFat(nutritionInfo.getTransFat())
                .cholesterol(nutritionInfo.getCholesterol())
                .sodium(nutritionInfo.getSodium())
                .potassium(nutritionInfo.getPotassium())
                .fiber(nutritionInfo.getFiber())
                .sugar(nutritionInfo.getSugar())
                .vitaminA(nutritionInfo.getVitaminA())
                .vitaminC(nutritionInfo.getVitaminC())
                .calcium(nutritionInfo.getCalcium())
                .iron(nutritionInfo.getIron())
                .build();
    }

    public List<NutritionInfoResponse> toNutritionInfoResponseList(List<NutritionInfo> nutritionInfoList) {
        if (nutritionInfoList == null) {
            return null;
        }

        return nutritionInfoList.stream()
                .map(this::toNutritionInfoResponse)
                .collect(Collectors.toList());
    }
}