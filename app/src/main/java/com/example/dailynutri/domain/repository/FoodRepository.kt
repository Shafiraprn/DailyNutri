package com.example.dailynutri.domain.repository

import com.example.dailynutri.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getFoodLibrary(userId: String): Flow<List<Food>>
    suspend fun addFood(food: Food)
    suspend fun deleteFood(foodId: String)

    // BARU: Fungsi untuk inisialisasi data bawaan jika kamus kosong
    suspend fun initializeDefaultLibrary(userId: String)
}