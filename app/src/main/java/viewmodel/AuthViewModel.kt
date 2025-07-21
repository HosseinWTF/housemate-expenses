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

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repo.login(email, password)
            _isLoggedIn.value = result.isSuccess
        }
    }
    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repo.register(email, password)
            _isLoggedIn.value = result.isSuccess
        }
    }
    fun logOut() {
        repo.logOut()
        _isLoggedIn.value = false
    }
    init {
        _isLoggedIn.value = repo.getCurrentUserId() != null
    }
    fun getCurrentUserId(): String? {
        return repo.getCurrentUserId()
    }


}