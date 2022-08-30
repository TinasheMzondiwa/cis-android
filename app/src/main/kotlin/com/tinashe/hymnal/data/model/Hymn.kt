package com.tinashe.hymnal.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "hymns")
@Parcelize
data class Hymn(
    @PrimaryKey(autoGenerate = true)
    val hymnId: Int = 0,
    val book: String,
    val number: Int,
    val title: String,
    val content: String,
    val majorKey: String?,
    var editedContent: String?
) : Parcelable
