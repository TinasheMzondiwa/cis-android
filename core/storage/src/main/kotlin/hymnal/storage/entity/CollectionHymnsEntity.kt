package hymnal.storage.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionHymnsEntity(
    @Embedded val collection: HymnCollectionEntity,

    @Relation(
        parentColumn = "collectionId",
        entityColumn = "hymnId",
        associateBy = Junction(CollectionHymnCrossRefEntity::class)
    )
    val hymns: List<HymnEntity>
)
