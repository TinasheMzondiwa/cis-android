package hymnal.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymnals")
data class HymnalEntity(
    @PrimaryKey
    val code: String,
    val title: String,
    val language: String
)
