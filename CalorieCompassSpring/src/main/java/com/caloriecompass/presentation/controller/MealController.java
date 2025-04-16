package com.caloriecompass.presentation.controller;

import com.caloriecompass.application.dto.response.MealEntryResponse;
import com.caloriecompass.application.dto.response.MealResponse;
import com.caloriecompass.domain.entity.Meal;
import com.caloriecompass.domain.entity.MealEntry;
import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.service.AuthService;
import com.caloriecompass.domain.service.MealService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final AuthService authService;

    @GetMapping("/daily")
    public String getDailyMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        Long userId = getUserIdFromAuthentication();
        List<Meal> meals = mealService.getMealsByUserAndDate(userId, date);

        Map<Meal.MealType, List<Meal>> mealsByType = meals.stream()
                .collect(Collectors.groupingBy(Meal::getMealType));

        List<MealResponse> mealResponses = new ArrayList<>();
        for (Map.Entry<Meal.MealType, List<Meal>> entry : mealsByType.entrySet()) {
            for (Meal meal : entry.getValue()) {
                mealResponses.add(convertToMealResponse(meal));
            }
        }

        BigDecimal totalCalories = mealResponses.stream()
                .map(MealResponse::getTotalCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = mealResponses.stream()
                .map(MealResponse::getTotalCarbohydrates)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = mealResponses.stream()
                .map(MealResponse::getTotalProtein)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = mealResponses.stream()
                .map(MealResponse::getTotalFat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("date", date);
        model.addAttribute("meals", mealResponses);
        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalFat", totalFat);
        model.addAttribute("mealTypes", Meal.MealType.values());

        return "meal/daily";
    }

    @PostMapping("/create")
    public String createMeal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealType") String mealType) {

        Long userId = getUserIdFromAuthentication();
        Meal.MealType type = Meal.MealType.valueOf(mealType);
        mealService.createMeal(userId, date, type);

        return "redirect:/meals/daily?date=" + date;
    }

    @GetMapping("/{mealId}/add-food")
    public String getAddFoodForm(@PathVariable Long mealId, Model model) {
        Meal meal = mealService.getMealById(mealId);
        model.addAttribute("meal", convertToMealResponse(meal));
        return "meal/add-food";
    }

    @PostMapping("/{mealId}/add-food")
    public String addFoodToMeal(
            @PathVariable Long mealId,
            @RequestParam String foodId,
            @RequestParam String servingId,
            @RequestParam Double servingAmount) {

        mealService.addFoodToMeal(mealId, foodId, servingId, servingAmount);
        Meal meal = mealService.getMealById(mealId);

        return "redirect:/meals/daily?date=" + meal.getDate();
    }

    @PostMapping("/{mealId}/remove-food/{entryId}")
    public String removeFoodFromMeal(
            @PathVariable Long mealId,
            @PathVariable Long entryId) {

        Meal meal = mealService.getMealById(mealId);
        LocalDate mealDate = meal.getDate();

        mealService.removeFoodFromMeal(mealId, entryId);

        return "redirect:/meals/daily?date=" + mealDate;
    }

    @GetMapping("/calendar")
    public String getCalendarView(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            Model model) {

        YearMonth currentYearMonth;
        if (yearMonth == null || yearMonth.isEmpty()) {
            currentYearMonth = YearMonth.now();
        } else {
            currentYearMonth = YearMonth.parse(yearMonth);
        }

        Long userId = getUserIdFromAuthentication();

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        List<Meal> monthMeals = mealService.getMealsByUserAndDateRange(userId, firstDayOfMonth, lastDayOfMonth);

        Map<LocalDate, BigDecimal> dailyCalories = new HashMap<>();

        for (Meal meal : monthMeals) {
            LocalDate mealDate = meal.getDate();
            BigDecimal totalCalories = meal.getEntries().stream()
                    .map(entry -> entry.getCalories() != null ? entry.getCalories() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dailyCalories.merge(mealDate, totalCalories, BigDecimal::add);
        }

        YearMonth previousMonth = currentYearMonth.minusMonths(1);
        YearMonth nextMonth = currentYearMonth.plusMonths(1);

        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int dayOfWeekValue = firstDayOfWeek.getValue(); // 1-7

        List<List<CalendarDay>> calendarWeeks = new ArrayList<>();
        List<CalendarDay> week = new ArrayList<>();

        int previousMonthDaysToShow = dayOfWeekValue - 1;
        if (previousMonthDaysToShow > 0) {
            LocalDate lastDayOfPreviousMonth = firstDayOfMonth.minusDays(1);
            for (int i = previousMonthDaysToShow - 1; i >= 0; i--) {
                LocalDate date = lastDayOfPreviousMonth.minusDays(i);
                week.add(new CalendarDay(date, dailyCalories.getOrDefault(date, null), false));
            }
        }

        for (int day = 1; day <= lastDayOfMonth.getDayOfMonth(); day++) {
            LocalDate date = firstDayOfMonth.withDayOfMonth(day);
            week.add(new CalendarDay(date, dailyCalories.getOrDefault(date, null), true));

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY || day == lastDayOfMonth.getDayOfMonth()) {
                calendarWeeks.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            LocalDate lastDayShown = week.get(week.size() - 1).getDate();
            LocalDate nextDay = lastDayShown.plusDays(1);

            while (nextDay.getDayOfWeek() != DayOfWeek.MONDAY) {
                week.add(new CalendarDay(nextDay, dailyCalories.getOrDefault(nextDay, null), false));
                nextDay = nextDay.plusDays(1);
            }

            calendarWeeks.add(week);
        }

        model.addAttribute("currentYearMonth", currentYearMonth.toString());
        model.addAttribute("previousMonth", previousMonth.toString());
        model.addAttribute("nextMonth", nextMonth.toString());
        model.addAttribute("calendarWeeks", calendarWeeks);

        return "meal/calendar";
    }

    @PostMapping("/{mealId}/delete")
    public String deleteMeal(@PathVariable Long mealId) {
        Meal meal = mealService.getMealById(mealId);
        LocalDate mealDate = meal.getDate();

        mealService.deleteMeal(mealId);

        return "redirect:/meals/daily?date=" + mealDate;
    }

    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Not authenticated");
        }

        String email = authentication.getName();
        User user = authService.findUserByEmail(email);
        return user.getId();
    }

    private MealResponse convertToMealResponse(Meal meal) {
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        List<MealEntryResponse> entryResponses = new ArrayList<>();

        for (MealEntry entry : meal.getEntries()) {
            MealEntryResponse entryResponse = MealEntryResponse.builder()
                    .id(entry.getId())
                    .foodId(entry.getFoodId())
                    .foodName(entry.getFoodName())
                    .foodBrand(entry.getFoodBrand())
                    .servingDescription(entry.getServingDescription())
                    .servingAmount(entry.getServingAmount())
                    .servingUnit(entry.getServingUnit())
                    .calories(entry.getCalories())
                    .carbohydrates(entry.getCarbohydrates())
                    .protein(entry.getProtein())
                    .fat(entry.getFat())
                    .build();

            entryResponses.add(entryResponse);

            totalCalories = totalCalories.add(entry.getCalories() != null ? entry.getCalories() : BigDecimal.ZERO);
            totalCarbs = totalCarbs.add(entry.getCarbohydrates() != null ? entry.getCarbohydrates() : BigDecimal.ZERO);
            totalProtein = totalProtein.add(entry.getProtein() != null ? entry.getProtein() : BigDecimal.ZERO);
            totalFat = totalFat.add(entry.getFat() != null ? entry.getFat() : BigDecimal.ZERO);
        }

        return MealResponse.builder()
                .id(meal.getId())
                .date(meal.getDate())
                .mealType(meal.getMealType().name())
                .mealTypeDisplayName(meal.getMealType().getDisplayName())
                .entries(entryResponses)
                .totalCalories(totalCalories)
                .totalCarbohydrates(totalCarbs)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .build();
    }

    @Data
    @AllArgsConstructor
    public static class CalendarDay {
        private LocalDate date;
        private BigDecimal totalCalories;
        private boolean currentMonth;
    }
}