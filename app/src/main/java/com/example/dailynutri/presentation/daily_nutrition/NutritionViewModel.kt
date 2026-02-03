package com.example.dailynutri.presentation.daily_nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.model.NutritionLog
import com.example.dailynutri.domain.repository.FoodRepository
import com.example.dailynutri.domain.repository.NutritionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

/**
 * State untuk layar Realita Makan.
 */
data class NutritionState(
    val logs: List<NutritionLog> = emptyList(),
    val foodLibrary: List<Food> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val userId: String? = null,
    val isLoading: Boolean = false
)

/**
 * ViewModel untuk mengelola catatan makan harian (Realita).
 * Update: Menambahkan fitur Edit dan Logika Reset List saat ganti tanggal (sesuai MealPlanner).
 */
class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state = _state.asStateFlow()

    private var logJob: Job? = null

    fun onDateSelected(date: Long) {
        _state.update { it.copy(selectedDate = date) }
        state.value.userId?.let { observeLogs(it, date) }
    }

    // Perbaikan: Terima parameter 'date' dan reset list agar bersih
    fun observeLogs(userId: String, date: Long = state.value.selectedDate) {
        if (userId.isBlank()) return

        // LOGIKA PENTING: Kosongkan list saat ganti tanggal agar tidak menumpuk
        _state.update {
            it.copy(
                userId = userId,
                selectedDate = date,
                isLoading = true,
                logs = emptyList()
            )
        }

        logJob?.cancel()

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

        logJob = viewModelScope.launch {
            try {
                nutritionRepository.getLogsByDateRange(userId, startTime, endTime).collect { logs ->
                    _state.update { it.copy(logs = logs, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    // FUNGSI BARU: Ambil log untuk diedit
    suspend fun getLogById(logId: String): NutritionLog? {
        return nutritionRepository.getLogById(logId)
    }

    fun addLog(log: NutritionLog) {
        viewModelScope.launch {
            nutritionRepository.addNutritionLog(log)
        }
    }

    // FUNGSI BARU: Update log
    fun updateLog(log: NutritionLog) {
        viewModelScope.launch {
            nutritionRepository.updateNutritionLog(log)
        }
    }

    fun deleteLog(logId: String) {
        val currentList = _state.value.logs
        _state.update { it.copy(logs = currentList.filter { it.id != logId }) }

        viewModelScope.launch {
            nutritionRepository.deleteNutritionLog(logId)
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