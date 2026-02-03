package com.example.dailynutri.presentation.meal_planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.model.Meal
import com.example.dailynutri.domain.model.NutritionLog
import com.example.dailynutri.domain.repository.FoodRepository
import com.example.dailynutri.domain.repository.MealRepository
import com.example.dailynutri.domain.repository.NutritionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

/**
 * State untuk layar Rencana Makan.
 */
data class MealPlannerState(
    val meals: List<Meal> = emptyList(),
    val foodLibrary: List<Food> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val userId: String? = null,
    val isLoading: Boolean = false
)

/**
 * ViewModel Meal Planner.
 * Update: Menambahkan fungsi getMealById dan updateMeal untuk fitur Edit.
 */
class MealPlannerViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlannerState())
    val state = _state.asStateFlow()

    private var mealJob: Job? = null

    fun onDateSelected(date: Long) {
        _state.update { it.copy(selectedDate = date) }
        state.value.userId?.let { observeMeals(it, date) }
    }

    fun observeMeals(userId: String, date: Long = state.value.selectedDate) {
        if (userId.isBlank()) return

        _state.update {
            it.copy(
                userId = userId,
                selectedDate = date,
                isLoading = true,
                meals = emptyList()
            )
        }

        viewModelScope.launch {
            foodRepository.initializeDefaultLibrary(userId)
        }

        mealJob?.cancel()

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis

        mealJob = viewModelScope.launch {
            try {
                mealRepository.getMealsByDateRange(userId, startTime, endTime).collect { meals ->
                    _state.update { it.copy(meals = meals, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    // FUNGSI BARU: Ambil data meal untuk diedit
    suspend fun getMealById(mealId: String): Meal? {
        return mealRepository.getMealById(mealId)
    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.addMeal(meal)
        }
    }

    // FUNGSI BARU: Update meal yang sudah ada
    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.updateMeal(meal)
            // Jika meal ini sudah completed/dicentang, idealnya kita update log realitanya juga.
            // Namun untuk keamanan data, kita biarkan log realita apa adanya (sejarah)
            // atau user harus uncheck-check lagi untuk refresh log.
        }
    }

    fun deleteMeal(mealId: String) {
        val currentList = _state.value.meals
        _state.update { it.copy(meals = currentList.filter { meal -> meal.id != mealId }) }

        viewModelScope.launch {
            mealRepository.deleteMeal(mealId)
            nutritionRepository.deleteLogByMealId(mealId)
        }
    }

    fun toggleMealCompletion(meal: Meal) {
        val newStatus = !meal.isCompleted

        _state.update { currentState ->
            currentState.copy(
                meals = currentState.meals.map {
                    if (it.id == meal.id) it.copy(isCompleted = newStatus) else it
                }
            )
        }

        viewModelScope.launch {
            mealRepository.updateMealCompletion(meal.id, newStatus)

            if (newStatus) {
                nutritionRepository.deleteLogByMealId(meal.id)

                val log = NutritionLog(
                    userId = meal.userId,
                    foodName = meal.title,
                    actualCalories = meal.calories,
                    actualProtein = meal.protein,
                    actualCarbs = meal.carbs,
                    actualFat = meal.fat,
                    consumptionTime = System.currentTimeMillis(),
                    mealId = meal.id
                )
                nutritionRepository.addNutritionLog(log)
            } else {
                nutritionRepository.deleteLogByMealId(meal.id)
            }
        }
    }

    fun loadFoodLibrary(userId: String?) {
        if (userId.isNullOrBlank()) return
        viewModelScope.launch {
            foodRepository.getFoodLibrary(userId).collect { foods ->
                _state.update { it.copy(foodLibrary = foods) }
            }
        }
    }
}