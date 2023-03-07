package hymnal.storage.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "collectionHymnsRef",
    primaryKeys = ["collectionId", "hymnId"],
    indices = [Index("hymnId", unique = false)]
)
class CollectionHymnCrossRefEntity(
    val collectionId: Int,
    val hymnId: Int
)
