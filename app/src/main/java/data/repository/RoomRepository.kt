package data.repository

import com.google.firebase.firestore.FirebaseFirestore
import data.model.Room
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createRoom(name: String, creatorId: String): Result<String> {
        return try {
            val room = Room(
                name = name,
                createdBy = creatorId,
                members = listOf(creatorId),
                emailRemindersEnabled = false,
                createdAt = System.currentTimeMillis()
            )

            val docRef = firestore.collection("rooms")
                .add(room)
                .await()


            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserRooms(userId: String): Flow<List<Room>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val rooms = snapshot.documents.mapNotNull { doc ->
                        val room = doc.toObject(Room::class.java)
                        room?.copy(id = doc.id)
                    }
                    trySend(rooms)
                }
            }
        awaitClose {
            listener.remove()
        }
    }



}