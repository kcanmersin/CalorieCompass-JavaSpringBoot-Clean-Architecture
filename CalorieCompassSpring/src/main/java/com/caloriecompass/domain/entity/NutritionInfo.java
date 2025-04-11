package com.caloriecompass.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionInfo {
    private String servingId;
    private String servingDescription;
    private BigDecimal servingAmount;
    private String servingUnit;

    private BigDecimal calories;
    private BigDecimal carbohydrates;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal saturatedFat;
    private BigDecimal polyunsaturatedFat;
    private BigDecimal monounsaturatedFat;
    private BigDecimal transFat;
    private BigDecimal cholesterol;
    private BigDecimal sodium;
    private BigDecimal potassium;
    private BigDecimal fiber;
    private BigDecimal sugar;
    private BigDecimal vitaminA;
    private BigDecimal vitaminC;
    private BigDecimal calcium;
    private BigDecimal iron;
}