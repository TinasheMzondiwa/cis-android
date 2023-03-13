package hymnal.content.impl.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JsonHymnal(
    val key: String,
    val title: String,
    val language: String
)