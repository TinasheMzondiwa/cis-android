package hymnal.content.impl

import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.HymnCollection
import hymnal.storage.entity.CollectionHymnsEntity
import hymnal.storage.entity.HymnEntity

internal fun HymnEntity.toHymn() = Hymn(
    hymnId, book, number, title, content, markdown, majorKey, editedContent
)

internal fun Hymn.toEntity() = HymnEntity(
    hymnId, book, number, title, html, markdown, majorKey, editedContent
)

internal fun CollectionHymnsEntity.toModel() = CollectionHymns(
    collection = HymnCollection(
        collection.collectionId,
        collection.title,
        collection.description,
        collection.created
    ),
    hymns = hymns.map { it.toHymn() }
)