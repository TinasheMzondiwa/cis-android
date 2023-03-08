package hymnal.content.impl.model

import com.squareup.moshi.JsonClass
import hymnal.storage.entity.HymnEntity

@JsonClass(generateAdapter = true)
internal data class JsonHymn(
    val title: String,
    val number: Int,
    val content: String
)  {
    fun toHymnEntity(book: String): HymnEntity = HymnEntity(
        hymnId = 0,
        book = book,
        number = number,
        title = title,
        content = if (content.contains(title)) content else "<h3>$title</h3>$content",
        majorKey = "",
        editedContent = null
    )
}
