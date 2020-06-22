package com.tinashe.hymnal.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "hymns")
@Parcelize
data class Hymn(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val book: String,
    val number: Int,
    val title: String,
    val content: String,
    val majorKey: String?,
    val collections: List<Int>
) : Parcelable
