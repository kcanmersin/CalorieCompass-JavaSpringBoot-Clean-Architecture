package com.caloriecompass.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealEntry {
    private Long id;
    private String foodId;
    private String foodName;
    private String foodBrand;
    private String servingId;
    private String servingDescription;
    private BigDecimal servingAmount;
    private String servingUnit;
    private BigDecimal calories;
    private BigDecimal carbohydrates;
    private BigDecimal protein;
    private BigDecimal fat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}