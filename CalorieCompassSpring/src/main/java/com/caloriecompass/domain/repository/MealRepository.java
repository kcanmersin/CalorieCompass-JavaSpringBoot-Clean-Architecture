package com.caloriecompass.domain.repository;

import com.caloriecompass.domain.entity.Meal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository {
    Meal save(Meal meal);

    Optional<Meal> findById(Long id);

    List<Meal> findByUserIdAndDate(Long userId, LocalDate date);

    List<Meal> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    void deleteById(Long id);
}