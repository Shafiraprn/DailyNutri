package com.example.dailynutri.data.repository

import com.example.dailynutri.domain.model.NutritionLog
import com.example.dailynutri.domain.repository.NutritionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementasi NutritionRepository.
 * Update: Implementasi getLogById dan updateNutritionLog.
 */
class NutritionRepositoryImpl : NutritionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val nutritionCollection = firestore.collection("nutrition_logs")

    override fun getLogsByUserId(userId: String): Flow<List<NutritionLog>> = callbackFlow {
        val subscription = nutritionCollection
            .whereEqualTo("userId", userId)
            .orderBy("consumptionTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(NutritionLog::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }

    override fun getLogsByDateRange(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLog>> = callbackFlow {
        val subscription = nutritionCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("consumptionTime", startTime)
            .whereLessThanOrEqualTo("consumptionTime", endTime)
            .orderBy("consumptionTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(NutritionLog::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }

    // IMPLEMENTASI BARU: Ambil satu log spesifik
    override suspend fun getLogById(logId: String): NutritionLog? {
        return try {
            val document = nutritionCollection.document(logId).get().await()
            document.toObject(NutritionLog::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun addNutritionLog(log: NutritionLog) {
        try {
            nutritionCollection.add(log).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // IMPLEMENTASI BARU: Update log yang ada
    override suspend fun updateNutritionLog(log: NutritionLog) {
        try {
            log.id?.let { id ->
                nutritionCollection.document(id).set(log).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteNutritionLog(logId: String) {
        try {
            nutritionCollection.document(logId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteLogByMealId(mealId: String) {
        try {
            val snapshot = nutritionCollection
                .whereEqualTo("mealId", mealId)
                .get()
                .await()

            for (document in snapshot.documents) {
                document.reference.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}