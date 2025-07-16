package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.data.repository.ExpenseRepository
import data.model.Expense
import data.model.UserBalance
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

    private val _userBalances = MutableStateFlow<List<UserBalance>>(emptyList())
    val userBalances: StateFlow<List<UserBalance>> = _userBalances.asStateFlow()


    fun loadExpenses() {
        viewModelScope.launch {
            repo.getExpenses(roomId).collect { expensesList ->
                _expenses.value = expensesList
                _userBalances.value = calculateBalances(expensesList)
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

    private fun calculateBalances(expenses: List<Expense>): List<UserBalance> {
        val balances = mutableMapOf<String, Double>()

        for (expense in expenses) {
            val total = expense.amount
            val splitCount = expense.sharedWith.size
            if (splitCount == 0) continue

            val splitAmount = total / splitCount

            for (userId in expense.sharedWith) {
                balances[userId] = (balances[userId] ?: 0.0) - splitAmount
            }

            val payer = expense.paidBy
            balances[payer] = (balances[payer] ?: 0.0) + total
        }

        return balances.map { (userId, balance) ->
            UserBalance(userId, String.format("%.2f", balance).toDouble())
        }.sortedByDescending { it.balance }
    }

}



