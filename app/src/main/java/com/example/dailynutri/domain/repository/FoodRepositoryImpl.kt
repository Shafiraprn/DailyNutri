package com.example.dailynutri.data.repository

import com.example.dailynutri.data.source.DefaultFoodData
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.repository.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FoodRepositoryImpl : FoodRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val foodCollection = firestore.collection("foods")

    override fun getFoodLibrary(userId: String): Flow<List<Food>> = callbackFlow {
        val subscription = foodCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val foods = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Food::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(foods)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun initializeDefaultLibrary(userId: String) {
        // Cek dulu apakah user sudah punya data makanan
        val currentFoods = getFoodLibrary(userId).first()

        // Jika masih kosong, masukkan 30 data default
        if (currentFoods.isEmpty()) {
            val defaultList = DefaultFoodData.getDefaultFoods(userId)
            val batch = firestore.batch()

            defaultList.forEach { food ->
                val docRef = foodCollection.document()
                batch.set(docRef, food)
            }
            batch.commit().await()
        }
    }

    override suspend fun addFood(food: Food) {
        foodCollection.add(food).await()
    }

    override suspend fun deleteFood(foodId: String) {
        foodCollection.document(foodId).delete().await()
    }
}