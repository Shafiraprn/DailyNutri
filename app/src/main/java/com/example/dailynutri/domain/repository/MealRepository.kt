package com.example.dailynutri.domain.repository

import com.example.dailynutri.domain.model.Meal
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak Interface untuk manajemen rencana makan.
 * Update: Menambahkan getMealById untuk fitur Edit.
 */
interface MealRepository {
    fun getMealsByUserId(userId: String): Flow<List<Meal>>

    fun getMealsByDateRange(userId: String, startTime: Long, endTime: Long): Flow<List<Meal>>

    // FUNGSI BARU: Ambil 1 data meal spesifik untuk diedit
    suspend fun getMealById(mealId: String): Meal?

    suspend fun addMeal(meal: Meal)

    suspend fun updateMeal(meal: Meal)

    suspend fun deleteMeal(mealId: String)

    suspend fun updateMealCompletion(mealId: String, isCompleted: Boolean)
}