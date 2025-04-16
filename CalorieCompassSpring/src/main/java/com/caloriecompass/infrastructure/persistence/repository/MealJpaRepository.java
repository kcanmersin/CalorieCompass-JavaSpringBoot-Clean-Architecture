package com.caloriecompass.infrastructure.persistence.repository;

import com.caloriecompass.infrastructure.persistence.entity.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealJpaRepository extends JpaRepository<MealEntity, Long> {
    List<MealEntity> findByUserIdAndDate(Long userId, LocalDate date);

    List<MealEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}