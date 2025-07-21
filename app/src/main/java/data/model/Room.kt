package data.model

data class Room(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val members: List<String> = emptyList(),
    val emailRemindersEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {

}

