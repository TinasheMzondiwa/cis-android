package hymnal.content.impl

import androidx.annotation.RawRes
import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.HymnCollection
import hymnal.content.model.Hymnals
import hymnal.storage.entity.CollectionHymnsEntity
import hymnal.storage.entity.HymnEntity

@RawRes
internal fun Hymnals.resId(): Int = when (this) {
    Hymnals.ENGLISH -> R.raw.english
    Hymnals.TSWANA -> R.raw.tswana
    Hymnals.SOTHO -> R.raw.sotho
    Hymnals.CHICHEWA -> R.raw.chichewa
    Hymnals.TONGA -> R.raw.tonga
    Hymnals.SHONA -> R.raw.shona
    Hymnals.VENDA -> R.raw.venda
    Hymnals.SWAHILI -> R.raw.swahili
    Hymnals.NDEBELE -> R.raw.ndebele
    Hymnals.XHOSA -> R.raw.xhosa
    Hymnals.XITSONGA -> R.raw.xitsonga
    Hymnals.GIKUYU -> R.raw.gikuyu
    Hymnals.ABAGUSII -> R.raw.abagusii
    Hymnals.DHOLUO -> R.raw.dholuo
    Hymnals.SDAH -> R.raw.sdah
}

internal fun HymnEntity.toHymn() = Hymn(
    hymnId, book, number, title, content, majorKey, editedContent
)

internal fun Hymn.toEntity() = HymnEntity(
    hymnId, book, number, title, content, majorKey, editedContent
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