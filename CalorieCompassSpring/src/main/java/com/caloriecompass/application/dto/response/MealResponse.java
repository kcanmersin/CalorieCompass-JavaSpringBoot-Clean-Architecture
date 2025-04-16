package com.caloriecompass.application.dto.response;

import com.caloriecompass.domain.entity.Meal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {
    private Long id;
    private LocalDate date;
    private String mealType;
    private String mealTypeDisplayName;
    private List<MealEntryResponse> entries;
    private BigDecimal totalCalories;
    private BigDecimal totalCarbohydrates;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
}