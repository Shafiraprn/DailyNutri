package com.example.dailynutri.data.source

import com.example.dailynutri.domain.model.Food

/**
 * Sumber data statis untuk 30 makanan bawaan.
 */
object DefaultFoodData {
    fun getDefaultFoods(userId: String): List<Food> {
        return listOf(
            // Karbohidrat
            Food(userId = userId, name = "Nasi Putih", category = "Karbohidrat", servingSize = "1 Piring", calories = 204, protein = 4.2, carbs = 44.0, fat = 0.4),
            Food(userId = userId, name = "Nasi Merah", category = "Karbohidrat", servingSize = "1 Piring", calories = 110, protein = 2.6, carbs = 23.0, fat = 0.9),
            Food(userId = userId, name = "Roti Gandum", category = "Karbohidrat", servingSize = "1 Lembar", calories = 67, protein = 2.4, carbs = 12.0, fat = 1.0),
            Food(userId = userId, name = "Kentang Rebus", category = "Karbohidrat", servingSize = "1 Buah Sedang", calories = 87, protein = 1.9, carbs = 20.0, fat = 0.1),
            Food(userId = userId, name = "Ubi Cilembu", category = "Karbohidrat", servingSize = "1 Buah", calories = 103, protein = 1.5, carbs = 24.0, fat = 0.1),

            // Protein Hewani
            Food(userId = userId, name = "Telur Rebus", category = "Protein", servingSize = "1 Butir", calories = 78, protein = 6.0, carbs = 0.6, fat = 5.0),
            Food(userId = userId, name = "Dada Ayam Bakar", category = "Protein", servingSize = "100g", calories = 165, protein = 31.0, carbs = 0.0, fat = 3.6),
            Food(userId = userId, name = "Ikan Kembung", category = "Protein", servingSize = "1 Ekor", calories = 112, protein = 18.0, carbs = 0.0, fat = 4.5),
            Food(userId = userId, name = "Daging Sapi", category = "Protein", servingSize = "100g", calories = 250, protein = 26.0, carbs = 0.0, fat = 15.0),
            Food(userId = userId, name = "Udang Rebus", category = "Protein", servingSize = "100g", calories = 99, protein = 24.0, carbs = 0.2, fat = 0.3),

            // Protein Nabati
            Food(userId = userId, name = "Tempe Bacem", category = "Protein", servingSize = "1 Potong", calories = 120, protein = 6.0, carbs = 10.0, fat = 7.0),
            Food(userId = userId, name = "Tahu Goreng", category = "Protein", servingSize = "1 Potong", calories = 35, protein = 2.5, carbs = 1.0, fat = 2.5),
            Food(userId = userId, name = "Kacang Hijau", category = "Protein", servingSize = "1 Mangkuk", calories = 212, protein = 14.0, carbs = 38.0, fat = 0.8),

            // Sayuran
            Food(userId = userId, name = "Bayam Rebus", category = "Sayuran", servingSize = "1 Mangkuk", calories = 23, protein = 2.9, carbs = 3.6, fat = 0.4),
            Food(userId = userId, name = "Brokoli", category = "Sayuran", servingSize = "100g", calories = 34, protein = 2.8, carbs = 7.0, fat = 0.4),
            Food(userId = userId, name = "Wortel", category = "Sayuran", servingSize = "1 Buah", calories = 41, protein = 0.9, carbs = 10.0, fat = 0.2),
            Food(userId = userId, name = "Kangkung", category = "Sayuran", servingSize = "1 Porsi", calories = 19, protein = 2.6, carbs = 3.1, fat = 0.4),
            Food(userId = userId, name = "Sawi Hijau", category = "Sayuran", servingSize = "100g", calories = 13, protein = 1.5, carbs = 2.2, fat = 0.2),

            // Buah-buahan
            Food(userId = userId, name = "Pisang", category = "Buah", servingSize = "1 Buah", calories = 89, protein = 1.1, carbs = 23.0, fat = 0.3),
            Food(userId = userId, name = "Apel", category = "Buah", servingSize = "1 Buah", calories = 52, protein = 0.3, carbs = 14.0, fat = 0.2),
            Food(userId = userId, name = "Alpukat", category = "Buah", servingSize = "1 Buah", calories = 160, protein = 2.0, carbs = 9.0, fat = 15.0),
            Food(userId = userId, name = "Pepaya", category = "Buah", servingSize = "1 Potong", calories = 43, protein = 0.5, carbs = 11.0, fat = 0.3),
            Food(userId = userId, name = "Jeruk", category = "Buah", servingSize = "1 Buah", calories = 47, protein = 0.9, carbs = 12.0, fat = 0.1),

            // Minuman & Camilan
            Food(userId = userId, name = "Susu Sapi", category = "Minuman", servingSize = "1 Gelas", calories = 146, protein = 8.0, carbs = 11.0, fat = 8.0),
            Food(userId = userId, name = "Kopi Hitam", category = "Minuman", servingSize = "1 Gelas", calories = 2, protein = 0.1, carbs = 0.0, fat = 0.0),
            Food(userId = userId, name = "Yogurt", category = "Minuman", servingSize = "1 Wadah", calories = 59, protein = 10.0, carbs = 3.6, fat = 0.4),
            Food(userId = userId, name = "Kurma", category = "Buah", servingSize = "3 Butir", calories = 60, protein = 0.4, carbs = 16.0, fat = 0.1),
            Food(userId = userId, name = "Kacang Almond", category = "Camilan", servingSize = "10 Butir", calories = 70, protein = 2.5, carbs = 2.5, fat = 6.0),
            Food(userId = userId, name = "Oatmeal", category = "Sereal", servingSize = "1 Mangkuk", calories = 150, protein = 5.0, carbs = 27.0, fat = 3.0),
            Food(userId = userId, name = "Madu", category = "Tambahan", servingSize = "1 SDM", calories = 64, protein = 0.1, carbs = 17.0, fat = 0.0)
        )
    }
}