package hymnal.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class HymnCollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,
    val title: String,
    val description: String?,
    val created: Long
)
