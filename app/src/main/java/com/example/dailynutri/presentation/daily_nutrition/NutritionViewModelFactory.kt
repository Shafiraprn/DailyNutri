package com.example.dailynutri.presentation.daily_nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailynutri.domain.repository.FoodRepository
import com.example.dailynutri.domain.repository.NutritionRepository

/**
 * Factory untuk NutritionViewModel.
 * Diperbarui untuk mendukung 2 repositori guna fitur Smart Auto-complete.
 * Memperbaiki error "Too many arguments" di NavGraph.
 */
class NutritionViewModelFactory(
    private val nutritionRepository: NutritionRepository,
    private val foodRepository: FoodRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionViewModel(
                nutritionRepository,
                foodRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: NutritionViewModel")
    }
}