package com.tinashe.hymnal.data.model.collections

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.HymnCollection

data class CollectionHymns(
    @Embedded val collection: HymnCollection,

    @Relation(
        parentColumn = "collectionId",
        entityColumn = "hymnId",
        associateBy = Junction(CollectionHymnCrossRef::class)
    )
    val hymns: List<Hymn>
)
