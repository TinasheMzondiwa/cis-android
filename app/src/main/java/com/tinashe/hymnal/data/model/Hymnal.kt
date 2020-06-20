package com.tinashe.hymnal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymnals")
data class Hymnal(
        @PrimaryKey
        val code: String,
        val title: String,
        val language: String
)