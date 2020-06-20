package com.tinashe.hymnal.data.model.remote

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.tinashe.hymnal.data.model.Hymn
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class RemoteHymn(
    val title: String,
    val number: Int,
    val content: String
) : Parcelable {
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
