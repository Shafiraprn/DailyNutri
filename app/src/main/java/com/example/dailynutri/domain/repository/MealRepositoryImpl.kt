package com.example.dailynutri.data.repository

import com.example.dailynutri.domain.model.Meal
import com.example.dailynutri.domain.repository.MealRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementasi MealRepository.
 * Update: Implementasi getMealById.
 */
class MealRepositoryImpl : MealRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val mealCollection = firestore.collection("meals")

    override fun getMealsByUserId(userId: String): Flow<List<Meal>> = callbackFlow {
        val subscription = mealCollection
            .whereEqualTo("userId", userId)
            .orderBy("scheduledDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val meals = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Meal::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(meals)
            }
        awaitClose { subscription.remove() }
    }

    override fun getMealsByDateRange(userId: String, startTime: Long, endTime: Long): Flow<List<Meal>> = callbackFlow {
        val subscription = mealCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("scheduledDate", startTime)
            .whereLessThanOrEqualTo("scheduledDate", endTime)
            .orderBy("scheduledDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val meals = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Meal::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(meals)
            }
        awaitClose { subscription.remove() }
    }

    // IMPLEMENTASI BARU: Ambil satu dokumen berdasarkan ID
    override suspend fun getMealById(mealId: String): Meal? {
        return try {
            val document = mealCollection.document(mealId).get().await()
            document.toObject(Meal::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun addMeal(meal: Meal) {
        mealCollection.add(meal).await()
    }

    override suspend fun updateMeal(meal: Meal) {
        meal.id?.let { id ->
            mealCollection.document(id).set(meal).await()
        }
    }

    override suspend fun deleteMeal(mealId: String) {
        mealCollection.document(mealId).delete().await()
    }

    override suspend fun updateMealCompletion(mealId: String, isCompleted: Boolean) {
        mealCollection.document(mealId).update("isCompleted", isCompleted).await()
    }
}