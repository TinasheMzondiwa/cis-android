package hymnal.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class HymnEntity(
    @PrimaryKey(autoGenerate = true)
    val hymnId: Int = 0,
    val book: String,
    val number: Int,
    val title: String,
    /** html content **/
    val content: String?,
    val markdown: String?,
    val majorKey: String?,
    val editedContent: String?
)
