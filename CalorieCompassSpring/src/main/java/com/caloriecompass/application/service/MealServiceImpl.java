package com.caloriecompass.application.service;

import com.caloriecompass.domain.entity.Food;
import com.caloriecompass.domain.entity.Meal;
import com.caloriecompass.domain.entity.MealEntry;
import com.caloriecompass.domain.entity.NutritionInfo;
import com.caloriecompass.domain.repository.MealRepository;
import com.caloriecompass.domain.service.FoodService;
import com.caloriecompass.domain.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final FoodService foodService;

    @Override
    @Transactional
    public Meal createMeal(Long userId, LocalDate date, Meal.MealType mealType) {
        Meal meal = Meal.builder()
                .userId(userId)
                .date(date)
                .mealType(mealType)
                .entries(new ArrayList<>())
                .build();

        return mealRepository.save(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public Meal getMealById(Long mealId) {
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + mealId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getMealsByUserAndDate(Long userId, LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getMealsByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional
    public MealEntry addFoodToMeal(Long mealId, String foodId, String servingId, double servingAmount) {
        Meal meal = getMealById(mealId);
        Food food = foodService.getFoodById(foodId);

        if (food == null) {
            throw new RuntimeException("Food not found with id: " + foodId);
        }

        List<NutritionInfo> nutritionInfos = foodService.getNutritionInfo(foodId);
        NutritionInfo selectedServing = nutritionInfos.stream()
                .filter(info -> info.getServingId().equals(servingId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Serving not found with id: " + servingId));

        BigDecimal servingAmountBD = BigDecimal.valueOf(servingAmount);
        BigDecimal ratio;

        if (selectedServing.getServingAmount() == null
                || selectedServing.getServingAmount().compareTo(BigDecimal.ZERO) == 0) {
            ratio = BigDecimal.ONE;
        } else {
            ratio = servingAmountBD.divide(selectedServing.getServingAmount(), 4, BigDecimal.ROUND_HALF_UP);
        }

        MealEntry entry = MealEntry.builder()
                .foodId(foodId)
                .foodName(food.getName())
                .foodBrand(food.getBrand())
                .servingId(selectedServing.getServingId())
                .servingDescription(selectedServing.getServingDescription())
                .servingAmount(servingAmountBD)
                .servingUnit(selectedServing.getServingUnit())
                .calories(multiplyNullSafe(selectedServing.getCalories(), ratio))
                .carbohydrates(multiplyNullSafe(selectedServing.getCarbohydrates(), ratio))
                .protein(multiplyNullSafe(selectedServing.getProtein(), ratio))
                .fat(multiplyNullSafe(selectedServing.getFat(), ratio))
                .build();

        meal.getEntries().add(entry);
        mealRepository.save(meal);

        return entry;
    }

    @Override
    @Transactional
    public void removeFoodFromMeal(Long mealId, Long entryId) {
        Meal meal = getMealById(mealId);
        meal.setEntries(meal.getEntries().stream()
                .filter(entry -> !entry.getId().equals(entryId))
                .collect(Collectors.toList()));
        mealRepository.save(meal);
    }

    @Override
    @Transactional
    public void deleteMeal(Long mealId) {
        mealRepository.deleteById(mealId);
    }

    private BigDecimal multiplyNullSafe(BigDecimal value, BigDecimal multiplier) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(multiplier);
    }
}