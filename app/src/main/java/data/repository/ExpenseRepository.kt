package com.yourpackage.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import data.model.Expense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun addExpense(roomId: String, expense: Expense): Result<Unit> {
        return try {
            val docRef = firestore.collection("rooms")
                .document(roomId)
                .collection("expenses")
                .add(expense)
                .await()


            firestore.collection("rooms")
                .document(roomId)
                .collection("expenses")
                .document(docRef.id)
                .update("id", docRef.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getExpenses(roomId: String): Flow<List<Expense>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .document(roomId)
            .collection("expenses")
            .orderBy("timestamp") // optional: sort by time
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val expenses = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Expense::class.java)?.copy(id = doc.id)
                    }
                    trySend(expenses)
                }
            }

        awaitClose {
            listener.remove()
        }
    }
}
