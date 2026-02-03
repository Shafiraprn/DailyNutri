package com.example.dailynutri.domain.model

/**
 * Representasi data murni untuk database makanan personal (Food Database).
 * Digunakan sebagai referensi master data nutrisi untuk mempermudah input
 * pada rencana maupun pencatatan harian.
 */
data class Food(
    val id: String = "",
    val userId: String = "", // UID dari Firebase Auth untuk isolasi library antar user
    val name: String = "",
    val category: String = "", // Contoh: Karbohidrat, Protein, Buah, Snack
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val servingSize: String = "", // Contoh: "100g", "1 Porsi", "1 Gelas"
    val isFavorite: Boolean = false
)