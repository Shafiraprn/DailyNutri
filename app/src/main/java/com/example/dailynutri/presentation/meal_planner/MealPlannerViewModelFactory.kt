package com.example.dailynutri.presentation.meal_planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailynutri.domain.repository.FoodRepository
import com.example.dailynutri.domain.repository.MealRepository
import com.example.dailynutri.domain.repository.NutritionRepository

/**
 * Factory diperbarui untuk mendukung 3 repositori.
 * Memperbaiki error "Too many arguments" di NavGraph.
 */
class MealPlannerViewModelFactory(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealPlannerViewModel(
                mealRepository,
                foodRepository,
                nutritionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: MealPlannerViewModel")
    }
}