package com.caloriecompass.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealEntryResponse {
    private Long id;
    private String foodId;
    private String foodName;
    private String foodBrand;
    private String servingDescription;
    private BigDecimal servingAmount;
    private String servingUnit;
    private BigDecimal calories;
    private BigDecimal carbohydrates;
    private BigDecimal protein;
    private BigDecimal fat;
}