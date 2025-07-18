package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.yourpackage.data.repository.ExpenseRepository
import data.model.Expense
import data.model.OwesRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private val _userMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val userMap: StateFlow<Map<String, String>> = _userMap.asStateFlow()

    fun loadExpenses() {
        viewModelScope.launch {
            repo.getExpenses(roomId).collect { expensesList ->
                _expenses.value = expensesList
                // No need to precalculate balances anymore
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

    fun loadUserNames(memberIds: List<String>) {
        viewModelScope.launch {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .whereIn("uid", memberIds)
                .get()
                .await()

            val map = snapshot.documents.associate { doc ->
                val uid = doc.id
                val name = doc.getString("name") ?: ""
                uid to name
            }

            _userMap.value = map
        }
    }

    fun calculateOwes(): List<OwesRecord> {
        val records = mutableListOf<OwesRecord>()

        for (expense in expenses.value) {
            val total = expense.amount
            val splitCount = expense.sharedWith.size
            if (splitCount == 0) continue

            val splitAmount = total / splitCount
            val payer = expense.paidBy

            for (userId in expense.sharedWith) {
                if (userId == payer) continue
                records.add(OwesRecord(fromUid = userId, toUid = payer, amount = splitAmount))
            }
        }

        return records
            .groupBy { it.fromUid to it.toUid }
            .map { (key, group) ->
                val (from, to) = key
                val totalOwed = group.sumOf { it.amount }
                OwesRecord(from, to, String.format("₺%.2f", totalOwed).toDouble())
            }
    }
}
