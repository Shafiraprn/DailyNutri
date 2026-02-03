package com.example.dailynutri.domain.repository

import com.example.dailynutri.domain.model.NutritionLog
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak Interface untuk manajemen catatan makan (Realita).
 * Update: Menambahkan fitur GetById dan Update agar bisa diedit seperti Rencana.
 */
interface NutritionRepository {
    fun getLogsByUserId(userId: String): Flow<List<NutritionLog>>

    fun getLogsByDateRange(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLog>>

    // FUNGSI BARU: Ambil 1 log untuk diedit
    suspend fun getLogById(logId: String): NutritionLog?

    suspend fun addNutritionLog(log: NutritionLog)

    // FUNGSI BARU: Simpan perubahan log
    suspend fun updateNutritionLog(log: NutritionLog)

    suspend fun deleteNutritionLog(logId: String)

    suspend fun deleteLogByMealId(mealId: String)
}