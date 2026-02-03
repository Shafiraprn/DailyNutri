package com.example.dailynutri.presentation.food_database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State untuk layar Kamus Makanan.
 */
data class FoodState(
    val foodLibrary: List<Food> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel untuk mengelola data kamus makanan.
 * Perbaikan: Menghapus FoodViewModelFactory dari sini karena sudah ada di file terpisah.
 */
class FoodViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FoodState())
    val state = _state.asStateFlow()

    fun observeFoodLibrary(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            foodRepository.getFoodLibrary(userId).collect { foods ->
                _state.update { it.copy(foodLibrary = foods, isLoading = false) }
            }
        }
    }

    fun addFood(food: Food) {
        viewModelScope.launch {
            foodRepository.addFood(food)
        }
    }

    fun deleteFood(foodId: String) {
        viewModelScope.launch {
            foodRepository.deleteFood(foodId)
        }
    }
}