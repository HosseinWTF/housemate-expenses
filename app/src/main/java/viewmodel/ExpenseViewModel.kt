package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.data.repository.ExpenseRepository
import data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val roomId: String,
    private val repo: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _addExpenseResult = MutableStateFlow<Result<Unit>?>(null)
    val addExpenseResult: StateFlow<Result<Unit>?> = _addExpenseResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadExpenses() {
        viewModelScope.launch {
            repo.getExpenses(roomId).collect { result ->
                _expenses.value = result
            }
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repo.addExpense(roomId, expense)
            _addExpenseResult.value = result
            _isLoading.value = false
        }
    }

    fun clearAddExpenseResult() {
        _addExpenseResult.value = null
    }
}



