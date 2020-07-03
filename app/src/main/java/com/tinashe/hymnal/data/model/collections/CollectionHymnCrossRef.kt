package com.tinashe.hymnal.data.model.collections

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "collectionHymnsRef",
    primaryKeys = ["collectionId", "hymnId"],
    indices = [Index("hymnId", unique = false)]
)
class CollectionHymnCrossRef(
    val collectionId: Int,
    val hymnId: Int
)
