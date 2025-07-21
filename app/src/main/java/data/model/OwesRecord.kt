package data.model

data class OwesRecord(
    val fromUid: String,
    val toUid: String,
    val amount: Double,
)
