package hymnal.content.api

import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import hymnal.content.model.HymnalHymns
import hymnal.content.model.TitleBody
import kotlinx.coroutines.flow.Flow

interface HymnalRepository {

    fun getHymnals(): Result<List<Hymnal>>

    fun getHymns(): Flow<Result<HymnalHymns>>

    fun setSelectedHymnal(hymnal: Hymnal)

    suspend fun searchHymns(query: String?): Result<List<Hymn>>

    fun updateHymn(hymn: Hymn)

    fun getCollectionHymns(): Flow<Result<List<CollectionHymns>>>

    suspend fun searchCollections(query: String?): Result<List<CollectionHymns>>

    fun addCollection(content: TitleBody)

    fun updateHymnCollections(hymnId: Int, collectionId: Int, add: Boolean)

    suspend fun getCollection(id: Int): Result<CollectionHymns>

    fun deleteCollection(collection: CollectionHymns)
}