package com.example.dailynutri.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynutri.domain.repository.MealRepository
import com.example.dailynutri.domain.repository.NutritionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel Dashboard.
 * Perbaikan: Menambahkan penanganan Error (Catch) agar tidak Force Close jika Index Firestore belum dibuat.
 */
class DashboardViewModel(
    private val mealRepository: MealRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState = _dashboardState.asStateFlow()

    private var dataJob: Job? = null

    fun getDashboardData(userId: String?) {
        if (userId.isNullOrBlank()) return

        dataJob?.cancel()

        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)

            // Hitung Rentang Waktu Hari Ini (00:00 - 23:59)
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            dataJob = launch {
                combine(
                    mealRepository.getMealsByDateRange(userId, startOfDay, endOfDay),
                    nutritionRepository.getLogsByDateRange(userId, startOfDay, endOfDay)
                ) { meals, logs ->
                    val plannedCal = meals.sumOf { it.calories }
                    val actualCal = logs.sumOf { it.actualCalories }
                    val remaining = plannedCal - actualCal

                    DashboardState(
                        plannedCalories = plannedCal,
                        actualCalories = actualCal,
                        remainingCalories = if (remaining < 0) 0 else remaining,
                        isOverLimit = actualCal > plannedCal,
                        isLoading = false
                    )
                }
                    .catch { e ->
                        // PERBAIKAN PENTING:
                        // Menangkap error (misal: Index Missing) agar aplikasi TIDAK Force Close.
                        e.printStackTrace()
                        _dashboardState.value = _dashboardState.value.copy(isLoading = false)
                    }
                    .collect { newState ->
                        _dashboardState.value = newState
                    }
            }
        }
    }
}

data class DashboardState(
    val plannedCalories: Int = 0,
    val actualCalories: Int = 0,
    val remainingCalories: Int = 0,
    val isOverLimit: Boolean = false,
    val isLoading: Boolean = false
)