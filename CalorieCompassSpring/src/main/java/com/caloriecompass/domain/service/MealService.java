package com.caloriecompass.domain.service;

import com.caloriecompass.domain.entity.Meal;
import com.caloriecompass.domain.entity.MealEntry;

import java.time.LocalDate;
import java.util.List;

public interface MealService {
    Meal createMeal(Long userId, LocalDate date, Meal.MealType mealType);

    Meal getMealById(Long mealId);

    List<Meal> getMealsByUserAndDate(Long userId, LocalDate date);

    List<Meal> getMealsByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    MealEntry addFoodToMeal(Long mealId, String foodId, String servingId, double servingAmount);

    void removeFoodFromMeal(Long mealId, Long entryId);

    void deleteMeal(Long mealId);
}