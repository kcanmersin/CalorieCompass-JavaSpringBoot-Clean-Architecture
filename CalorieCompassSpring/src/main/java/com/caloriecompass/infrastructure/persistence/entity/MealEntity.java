package com.caloriecompass.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, name = "meal_type")
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealEntryEntity> entries = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
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