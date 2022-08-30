package com.tinashe.hymnal.data.model.collections

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class HymnCollection(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,
    val title: String,
    var description: String?,
    val created: Long
)
