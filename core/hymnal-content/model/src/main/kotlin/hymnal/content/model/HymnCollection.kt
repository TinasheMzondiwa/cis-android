package hymnal.content.model

data class HymnCollection(
    val collectionId: Int,
    val title: String,
    val description: String?,
    val created: Long
)
