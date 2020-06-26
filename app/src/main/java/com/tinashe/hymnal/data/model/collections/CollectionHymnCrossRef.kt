package com.tinashe.hymnal.data.model.collections

import androidx.room.Entity

@Entity(tableName = "collectionHymnsRef", primaryKeys = ["collectionId", "hymnId"])
class CollectionHymnCrossRef(
    val collectionId: Int,
    val hymnId: Int
)
