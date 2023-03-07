package com.tinashe.hymnal.repository

import android.content.Context
import android.content.res.Resources
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tinashe.hymnal.data.model.constants.Hymnals
import com.tinashe.hymnal.data.model.remote.RemoteHymn
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.HymnCollection
import hymnal.content.model.Hymnal
import hymnal.content.model.HymnalHymns
import hymnal.content.model.TitleBody
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.CollectionHymnCrossRefEntity
import hymnal.storage.entity.CollectionHymnsEntity
import hymnal.storage.entity.HymnCollectionEntity
import hymnal.storage.entity.HymnEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
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
        val hymns = hymnsDao.listAll(code).map { it.toHymn() }
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

        val jsonString = getJsonResource(context.resources, res)
        val hymns = parseJson(jsonString) ?: emptyList()
        hymnsDao.insertAll(hymns.map { it.toHymnEntity(code) })
    }

    private fun getJsonResource(resources: Resources, resId: Int): String {
        val resourceReader = resources.openRawResource(resId)
        val writer = StringWriter()

        try {
            val reader = BufferedReader(InputStreamReader(resourceReader, "UTF-8") as Reader)
            var line: String? = reader.readLine()
            while (line != null) {
                writer.write(line)
                line = reader.readLine()
            }
            return writer.toString()
        } catch (e: Exception) {
            Timber.e("Unhandled exception while using JSONResourceReader")
        } finally {
            try {
                resourceReader.close()
            } catch (e: Exception) {
                Timber.e(e, "Unhandled exception while using JSONResourceReader")
            }
        }
        return ""
    }

    private fun parseJson(jsonString: String): List<RemoteHymn>? {
        val listDataType: Type =
            Types.newParameterizedType(List::class.java, RemoteHymn::class.java)
        val adapter: JsonAdapter<List<RemoteHymn>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

    suspend fun searchHymns(query: String?): List<Hymn> {
        return hymnsDao.search(selectedCode, "%${query ?: ""}%").map { it.toHymn() }
    }

    suspend fun updateHymn(hymn: Hymn) {
        hymnsDao.update(hymn.toEntity())
    }

    fun getCollectionHymns(): Flow<List<CollectionHymns>> = collectionsDao
        .getCollectionsWithHymns()
        .map { entities -> entities.map { it.toModel()} }

    suspend fun searchCollections(query: String?): List<CollectionHymns> =
        collectionsDao.searchFor("%${query ?: ""}%")
            .map { it.toModel() }

    suspend fun addCollection(content: TitleBody) {
        val collection =
            HymnCollectionEntity(
                title = content.title,
                description = content.body,
                created = System.currentTimeMillis()
            )
        collectionsDao.insert(collection)
    }

    suspend fun updateHymnCollections(hymnId: Int, collectionId: Int, add: Boolean) {
        if (add) {
            collectionsDao.insertRef(CollectionHymnCrossRefEntity(collectionId, hymnId))
        } else {
            collectionsDao.findRef(hymnId, collectionId)?.let {
                collectionsDao.deleteRef(it)
            }
        }
    }

    suspend fun getCollection(id: Int): Resource<CollectionHymns> {
        return collectionsDao.findById(id)?.let {
            Resource.success(it.toModel())
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

    private fun HymnEntity.toHymn() = Hymn(
        hymnId, book, number, title, content, majorKey, editedContent
    )

    private fun Hymn.toEntity() = HymnEntity(
        hymnId, book, number, title, content, majorKey, editedContent
    )

    private fun CollectionHymnsEntity.toModel() = CollectionHymns(
        collection = HymnCollection(
            collection.collectionId,
            collection.title,
            collection.description,
            collection.created
        ),
        hymns = hymns.map { it.toHymn() }
    )
}
