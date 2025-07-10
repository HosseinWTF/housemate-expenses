package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: FirebaseAuthRepository = FirebaseAuthRepository()
): ViewModel() {

    private val _isLoggedin = MutableStateFlow(false)
    val isLoggedin: StateFlow<Boolean> = _isLoggedin

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repo.login(email, password)
            _isLoggedin.value = result.isSuccess
        }
    }
    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repo.register(email, password)
            _isLoggedin.value = result.isSuccess
        }
    }
    fun logOut() {
        repo.logOut()
        _isLoggedin.value = false
    }
    init {
        _isLoggedin.value = repo.getCurrentUserId() != null
    }

}