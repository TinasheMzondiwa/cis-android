package com.tinashe.hymnal.repository

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.HymnalHymns
import com.tinashe.hymnal.data.model.TitleBody
import com.tinashe.hymnal.data.model.collections.CollectionHymnCrossRef
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.data.model.collections.HymnCollection
import com.tinashe.hymnal.data.model.constants.Hymnals
import com.tinashe.hymnal.data.model.remote.RemoteHymn
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

class HymnalRepository(
    private val context: Context,
    private val moshi: Moshi,
    private val hymnsDao: HymnsDao,
    private val collectionsDao: CollectionsDao,
    private val prefs: HymnalPrefs,
    private val backgroundContext: CoroutineContext = Dispatchers.IO
) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    fun getHymnals(): Resource<List<Hymnal>> {
        val hymnals = Hymnals.values().map {
            Hymnal(it.key, it.title, it.language)
        }
        return Resource.success(hymnals)
    }

    fun getHymns(selectedHymnal: Hymnal? = null) = flow {
        val code = selectedHymnal?.code ?: selectedCode
        if (hymnsDao.listAll(code).isEmpty()) {
            emit(Resource.loading())
            loadHymns(code)
        }

        prefs.setSelectedHymnal(code)

        val hymnal = getHymnal(code)
        val hymns = hymnsDao.listAll(code)
        emit(Resource.success(HymnalHymns(hymnal, hymns)))
    }.catch {
        Timber.e(it)
        emit(Resource.error(it))
    }.flowOn(backgroundContext)

    private fun getHymnal(code: String): Hymnal {
        return Hymnals.fromString(code)?.let {
            Hymnal(it.key, it.title, it.language)
        } ?: throw IllegalArgumentException("Invalid Hymnal code")
    }

    private suspend fun loadHymns(code: String) {
        val res = Hymnals.fromString(code)?.resId ?: return

        val jsonString = Helper.getJson(context.resources, res)
        val hymns = parseJson(jsonString) ?: emptyList()
        hymnsDao.insertAll(hymns.map { it.toHymn(code) })
    }

    private fun parseJson(jsonString: String): List<RemoteHymn>? {
        val listDataType: Type =
            Types.newParameterizedType(List::class.java, RemoteHymn::class.java)
        val adapter: JsonAdapter<List<RemoteHymn>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

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
