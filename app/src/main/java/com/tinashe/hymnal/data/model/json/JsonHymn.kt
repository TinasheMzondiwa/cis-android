package com.tinashe.hymnal.data.model.json

import com.squareup.moshi.JsonClass
import com.tinashe.hymnal.data.model.Hymn

@JsonClass(generateAdapter = true)
data class JsonHymn(
    val title: String,
    val number: Int,
    val content: String
) {
    fun toHymn(book: String): Hymn = Hymn(
        id = 0,
        book = book,
        number = number,
        title = title,
        content = content,
        collections = emptyList(),
        majorKey = ""
    )
}