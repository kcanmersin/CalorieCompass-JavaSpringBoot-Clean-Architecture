package com.caloriecompass.infrastructure.persistence.adapter;

import com.caloriecompass.domain.entity.Meal;
import com.caloriecompass.domain.entity.MealEntry;
import com.caloriecompass.domain.repository.MealRepository;
import com.caloriecompass.infrastructure.persistence.entity.MealEntity;
import com.caloriecompass.infrastructure.persistence.entity.MealEntryEntity;
import com.caloriecompass.infrastructure.persistence.repository.MealJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealRepositoryAdapter implements MealRepository {

    private final MealJpaRepository mealJpaRepository;

    @Override
    public Meal save(Meal meal) {
        MealEntity mealEntity = toMealEntity(meal);
        MealEntity savedEntity = mealJpaRepository.save(mealEntity);
        return toMeal(savedEntity);
    }

    @Override
    public Optional<Meal> findById(Long id) {
        return mealJpaRepository.findById(id)
                .map(this::toMeal);
    }

    @Override
    public List<Meal> findByUserIdAndDate(Long userId, LocalDate date) {
        return mealJpaRepository.findByUserIdAndDate(userId, date)
                .stream()
                .map(this::toMeal)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealJpaRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::toMeal)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mealJpaRepository.deleteById(id);
    }

    private MealEntity toMealEntity(Meal meal) {
        MealEntity entity = MealEntity.builder()
                .id(meal.getId())
                .date(meal.getDate())
                .mealType(MealEntity.MealType.valueOf(meal.getMealType().name()))
                .userId(meal.getUserId())
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
        if (meal.getEntries() != null) {
            entity.setEntries(meal.getEntries().stream()
                    .map(entry -> {
                        MealEntryEntity entryEntity = MealEntryEntity.builder()
                                .id(entry.getId())
                                .foodId(entry.getFoodId())
                                .foodName(entry.getFoodName())
                                .foodBrand(entry.getFoodBrand())
                                .servingId(entry.getServingId())
                                .servingDescription(entry.getServingDescription())
                                .servingAmount(entry.getServingAmount())
                                .servingUnit(entry.getServingUnit())
                                .calories(entry.getCalories())
                                .carbohydrates(entry.getCarbohydrates())
                                .protein(entry.getProtein())
                                .fat(entry.getFat())
                                .createdAt(entry.getCreatedAt())
                                .updatedAt(entry.getUpdatedAt())
                                .build();
                        entryEntity.setMeal(entity);
                        return entryEntity;
                    })
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private Meal toMeal(MealEntity entity) {
        Meal meal = Meal.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .mealType(Meal.MealType.valueOf(entity.getMealType().name()))
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getEntries() != null) {
            meal.setEntries(entity.getEntries().stream()
                    .map(entryEntity -> MealEntry.builder()
                            .id(entryEntity.getId())
                            .foodId(entryEntity.getFoodId())
                            .foodName(entryEntity.getFoodName())
                            .foodBrand(entryEntity.getFoodBrand())
                            .servingId(entryEntity.getServingId())
                            .servingDescription(entryEntity.getServingDescription())
                            .servingAmount(entryEntity.getServingAmount())
                            .servingUnit(entryEntity.getServingUnit())
                            .calories(entryEntity.getCalories())
                            .carbohydrates(entryEntity.getCarbohydrates())
                            .protein(entryEntity.getProtein())
                            .fat(entryEntity.getFat())
                            .createdAt(entryEntity.getCreatedAt())
                            .updatedAt(entryEntity.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList()));
        }

        return meal;
    }
}