package com.example.dailynutri.domain.model

/**
 * Model data untuk Rencana Makan (NIAT).
 * Diperbarui untuk mendukung fitur checklist (isCompleted).
 */
data class Meal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val mealType: String = "", // Sarapan, Makan Siang, dll
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val scheduledDate: Long = System.currentTimeMillis(), // Waktu rencana
    val isCompleted: Boolean = false // TRUE jika sudah dicentang/direalisasikan
) {
    // Constructor kosong diperlukan untuk Firebase Firestore
    constructor() : this("", "", "", "", 0, 0.0, 0.0, 0.0, 0L, false)
}