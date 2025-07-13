package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.Room
import data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    private val repo: RoomRepository = RoomRepository(),
): ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _createResult = MutableStateFlow<Result<String>?>(null)
    val createResult: StateFlow<Result<String>?> = _createResult.asStateFlow()

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
}