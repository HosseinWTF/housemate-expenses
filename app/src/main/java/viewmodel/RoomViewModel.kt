package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import data.model.Room
import data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoomViewModel(
    private val repo: RoomRepository = RoomRepository(),
): ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _createResult = MutableStateFlow<Result<String>?>(null)
    val createResult: StateFlow<Result<String>?> = _createResult.asStateFlow()

    private val _members = MutableStateFlow<List<String>>(emptyList())
    val members: StateFlow<List<String>> = _members.asStateFlow()

    private val _memberNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val memberNames: StateFlow<Map<String, String>> = _memberNames.asStateFlow()


    fun createRoom(name: String, userId: String) {
        viewModelScope.launch {
            repo.getUserRooms(userId).collect { roomList ->
                _rooms.value = roomList
            }
        }
    }
    fun startListeningToUserRooms(userId: String) {
        viewModelScope.launch {
            repo.getUserRooms(userId).collect { roomList ->
                _rooms.value = roomList
            }
        }
    }

    fun loadRoomMembers(roomId: String) {
        viewModelScope.launch {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("rooms")
                .document(roomId)
                .get()
                .await()

            val memberIds = snapshot.get("members") as? List<String> ?: emptyList()
            _members.value = memberIds

            val userSnapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .whereIn("uid", memberIds)
                .get()
                .await()

            val namesMap = userSnapshot.documents.associate {
                it.id to (it.getString("name") ?: "")
            }

            _memberNames.value = namesMap
        }
    }

    fun addMemberToRoom(roomId: String, userIdToAdd: String) {
        viewModelScope.launch {
            val roomRef = FirebaseFirestore.getInstance()
                .collection("rooms")
                .document(roomId)

            roomRef.update("members", FieldValue.arrayUnion(userIdToAdd)).await()

            loadRoomMembers(roomId)
        }
    }
    private val _joinResult = MutableStateFlow<Result<Unit>?>(null)
    val joinResult: StateFlow<Result<Unit>?> = _joinResult

    fun joinRoom(roomId: String, userId: String) {
        viewModelScope.launch {
            _joinResult.value = repo.joinRoomByCode(roomId, userId)
        }
    }

    fun clearJoinResult() {
        _joinResult.value = null
    }

}