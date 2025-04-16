package com.caloriecompass.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    private Long id;
    private LocalDate date;
    private MealType mealType;
    private Long userId;
    private List<MealEntry> entries = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum MealType {
        BREAKFAST("Kahvaltı"),
        LUNCH("Öğle Yemeği"),
        DINNER("Akşam Yemeği"),
        SNACK("Ara Öğün");

        private final String displayName;

        MealType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}