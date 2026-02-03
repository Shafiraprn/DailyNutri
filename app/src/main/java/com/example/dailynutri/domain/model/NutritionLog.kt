package com.example.dailynutri.domain.model

/**
 * Model data untuk Catatan Makan (REALITA).
 * Diperbarui dengan mealId untuk menghubungkan dengan rencana.
 */
data class NutritionLog(
    val id: String = "",
    val userId: String = "",
    val foodName: String = "",
    val actualCalories: Int = 0,
    val actualProtein: Double = 0.0,
    val actualCarbs: Double = 0.0,
    val actualFat: Double = 0.0,
    val consumptionTime: Long = System.currentTimeMillis(), // Waktu makan
    val mealId: String? = null // Menghubungkan ke ID Meal jika ini hasil centang rencana
) {
    // Constructor kosong diperlukan untuk Firebase Firestore
    constructor() : this("", "", "", 0, 0.0, 0.0, 0.0, 0L, null)
}