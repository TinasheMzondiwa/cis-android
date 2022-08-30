package com.tinashe.hymnal.data.model.remote

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.tinashe.hymnal.data.model.Hymn
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class RemoteHymn(
    val title: String,
    val number: Int,
    val content: String
) : Parcelable {
    fun toHymn(book: String): Hymn = Hymn(
        hymnId = 0,
        book = book,
        number = number,
        title = title,
        content = if (content.contains(title)) content else "<h3>$title</h3>$content",
        majorKey = "",
        editedContent = null
    )
}
