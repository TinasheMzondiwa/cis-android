package hymnal.content.impl.model

import com.squareup.moshi.JsonClass
import hymnal.storage.entity.HymnEntity

@JsonClass(generateAdapter = true)
internal data class JsonHymn(
    val title: String,
    val number: Int,
    val content: String?,
    val markdown: String?,
    val key: String?
)  {
    fun toHymnEntity(book: String): HymnEntity = HymnEntity(
        hymnId = 0,
        book = book,
        number = number,
        title = title,
        content = content?.let { if (it.contains(title)) it else "<h3>$title</h3>$it" },
        markdown = markdown,
        majorKey = key,
        editedContent = null
    )
}
