package com.tinashe.hymnal.data.repository

import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.HymnalHymns
import com.tinashe.hymnal.data.model.TitleBody
import com.tinashe.hymnal.data.model.collections.CollectionHymnCrossRef
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.data.model.collections.HymnCollection
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class HymnalRepository(
    private val hymnalsDao: HymnalsDao,
    private val hymnsDao: HymnsDao,
    private val collectionsDao: CollectionsDao,
    private val prefs: HymnalPrefs,
    private val remoteHymnsRepository: RemoteHymnsRepository,
    private val backgroundContext: CoroutineContext = Dispatchers.IO
) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    suspend fun getHymnals(): Resource<List<Hymnal>> = Resource.success(hymnalsDao.listAll())

    fun getHymns(selectedHymnal: Hymnal? = null) = flow {
        val hymnal = hymnalsDao.findByCode(selectedHymnal?.code ?: selectedCode)

        when {
            hymnal == null && hymnalsDao.findByCode(selectedCode) == null -> {
                emit(Resource.loading())

                val sample = remoteHymnsRepository.getSample()
                sample?.let { json ->
                    hymnalsDao.insert(Hymnal(json.key, json.title, json.language))
                    hymnsDao.insertAll(json.hymns.map { it.toHymn(json.key) })
                    prefs.setSelectedHymnal(json.key)
                }
            }
            selectedHymnal != null && hymnalsDao.findByCode(selectedHymnal.code) == null -> {
                emit(Resource.loading())
                // fetch remote hymnal
                val resource = remoteHymnsRepository.downloadHymns(
                    selectedHymnal.code
                )

                if (resource.isSuccessFul) {
                    resource.data?.let { json ->
                        hymnalsDao.insert(
                            Hymnal(
                                selectedHymnal.code,
                                selectedHymnal.title,
                                selectedHymnal.language
                            )
                        )
                        hymnsDao.insertAll(json.map { it.toHymn(selectedHymnal.code) })
                        prefs.setSelectedHymnal(selectedHymnal.code)
                    }
                } else {
                    emit(Resource.error(Exception("Error downloading hymnal file")))
                    return@flow
                }
            }
            hymnal != null -> {
                prefs.setSelectedHymnal(hymnal.code)
            }
        }

        hymnalsDao.findByCode(selectedCode)?.let {
            val hymns = hymnsDao.listAll(selectedCode)
            emit(Resource.success(HymnalHymns(it, hymns)))
        }
    }.catch {
        Timber.e(it)
        emit(Resource.error(it))
    }.flowOn(backgroundContext)

    suspend fun searchHymns(query: String?): List<Hymn> {
        return hymnsDao.search(selectedCode, "%${query ?: ""}%")
    }

    suspend fun updateHymn(hymn: Hymn) {
        hymnsDao.update(hymn)
    }

    fun getCollectionHymns(): Flow<List<CollectionHymns>> = collectionsDao.getCollectionsWithHymns()

    suspend fun searchCollections(query: String?): List<CollectionHymns> =
        collectionsDao.searchFor("%${query ?: ""}%")

    suspend fun addCollection(content: TitleBody) {
        val collection =
            HymnCollection(
                title = content.title,
                description = content.body,
                created = System.currentTimeMillis()
            )
        collectionsDao.insert(collection)
    }

    suspend fun updateHymnCollections(hymnId: Int, collectionId: Int, add: Boolean) {
        if (add) {
            collectionsDao.insertRef(CollectionHymnCrossRef(collectionId, hymnId))
        } else {
            collectionsDao.findRef(hymnId, collectionId)?.let {
                collectionsDao.deleteRef(it)
            }
        }
    }

    suspend fun getCollection(id: Int): Resource<CollectionHymns> {
        return collectionsDao.findById(id)?.let {
            Resource.success(it)
        } ?: Resource.error(IllegalArgumentException("Invalid Collection Id"))
    }

    suspend fun deleteCollection(collection: CollectionHymns) {
        val collectionId = collection.collection.collectionId
        collection.hymns.forEach { hymn ->
            collectionsDao.findRef(hymn.hymnId, collectionId)?.let {
                collectionsDao.deleteRef(it)
            }
        }
        collectionsDao.deleteCollection(collectionId)
    }
}
