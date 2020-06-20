package com.tinashe.hymnal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class Hymn(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val book: String,
    val number: Int,
    val title: String,
    val content: String,
    val majorKey: String?,
    val collections: List<Int>
)
