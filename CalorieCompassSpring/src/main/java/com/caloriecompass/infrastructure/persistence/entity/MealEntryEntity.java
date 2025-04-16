package com.caloriecompass.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private MealEntity meal;

    @Column(nullable = false, name = "food_id")
    private String foodId;

    @Column(nullable = false, name = "food_name")
    private String foodName;

    @Column(name = "food_brand")
    private String foodBrand;

    @Column(name = "serving_id")
    private String servingId;

    @Column(name = "serving_description")
    private String servingDescription;

    @Column(nullable = false, name = "serving_amount")
    private BigDecimal servingAmount;

    @Column(name = "serving_unit")
    private String servingUnit;

    @Column(nullable = false)
    private BigDecimal calories;

    @Column
    private BigDecimal carbohydrates;

    @Column
    private BigDecimal protein;

    @Column
    private BigDecimal fat;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}