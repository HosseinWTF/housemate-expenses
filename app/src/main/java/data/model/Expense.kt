package data.model

data class Expense(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val sharedWith: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)