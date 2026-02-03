package com.example.dailynutri.presentation.food_database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailynutri.domain.repository.FoodRepository

/**
 * Factory untuk membuat instance FoodViewModel dengan parameter FoodRepository.
 */
class FoodViewModelFactory(
    private val repository: FoodRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: FoodViewModel")
    }
}