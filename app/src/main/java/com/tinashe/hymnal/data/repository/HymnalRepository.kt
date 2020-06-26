package com.tinashe.hymnal.data.repository

import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.HymnCollection
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.HymnalHymns
import com.tinashe.hymnal.data.model.remote.RemoteHymnal
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

    fun getHymns(selectedHymnal: RemoteHymnal? = null) = flow {
        val hymnal = hymnalsDao.findByCode(selectedHymnal?.key ?: selectedCode)

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
            selectedHymnal != null && hymnalsDao.findByCode(selectedHymnal.key) == null -> {
                emit(Resource.loading())
                // fetch remote hymnal
                val resource = remoteHymnsRepository.downloadHymns(
                    selectedHymnal.key
                )

                if (resource.isSuccessFul) {
                    resource.data?.let { json ->
                        hymnalsDao.insert(Hymnal(selectedHymnal.key, selectedHymnal.title, selectedHymnal.language))
                        hymnsDao.insertAll(json.map { it.toHymn(selectedHymnal.key) })
                        prefs.setSelectedHymnal(selectedHymnal.key)
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

    fun getCollections(): Flow<List<HymnCollection>> = collectionsDao.listAll()

    suspend fun searchCollections(query: String?): List<HymnCollection> {
        return collectionsDao.searchFor("%${query ?: ""}%")
    }
}
