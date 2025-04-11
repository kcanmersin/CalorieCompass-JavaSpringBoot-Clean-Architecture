package com.caloriecompass.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    private String id;
    private String name;
    private String brand;
    private String description;
    private String foodType;
}