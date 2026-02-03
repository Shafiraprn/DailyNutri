package com.example.dailynutri.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailynutri.domain.model.Meal
import com.example.dailynutri.domain.repository.MealRepository
import com.example.dailynutri.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Factory untuk membuat instance DashboardViewModel dengan parameter Repository.
 * Dibutuhkan karena ViewModel standar tidak mendukung konstruktor berparameter secara langsung.
 */
class DashboardViewModelFactory(
    private val mealRepository: MealRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(mealRepository, nutritionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * ViewModel untuk mengelola fitur Perencanaan Makan (NIAT).
 * Menangani logika CRUD untuk rencana nutrisi masa depan.
 */
class MealPlannerViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlannerState())
    val state: StateFlow<MealPlannerState> = _state.asStateFlow()

    /**
     * Mengambil daftar rencana makan user secara real-time.
     */
    fun observeMeals(userId: String) {
        viewModelScope.launch {
            repository.getMealsByUserId(userId).collect { meals ->
                _state.value = _state.value.copy(meals = meals)
            }
        }
    }

    /**
     * Menambah rencana makan baru (NIAT).
     */
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            repository.addMeal(meal)
        }
    }

    /**
     * Menghapus rencana makan.
     */
    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }
}

/**
 * State penampung data untuk layar Meal Planner.
 */
data class MealPlannerState(
    val meals: List<Meal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)