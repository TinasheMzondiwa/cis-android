package hymnal.content.impl

import android.content.Context
import android.content.res.Resources
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import hymnal.android.coroutines.DispatcherProvider
import hymnal.content.api.HymnalRepository
import hymnal.content.impl.model.JsonHymn
import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import hymnal.content.model.HymnalHymns
import hymnal.content.model.Hymnals
import hymnal.content.model.TitleBody
import hymnal.prefs.HymnalPrefs
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.CollectionHymnCrossRefEntity
import hymnal.storage.entity.HymnCollectionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class HymnalRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val hymnsDao: HymnsDao,
    private val collectionsDao: CollectionsDao,
    private val prefs: HymnalPrefs,
    private val dispatcherProvider: DispatcherProvider
) : HymnalRepository, CoroutineScope by CoroutineScope(dispatcherProvider.io) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    override fun getHymnals(): Result<List<Hymnal>> {
        val hymnals = Hymnals.values().map {
            Hymnal(it.key, it.title, it.language)
        }
        return Result.success(hymnals)
    }

    override fun getHymns(selectedHymnal: Hymnal?): Flow<Result<HymnalHymns>> {
        return flow {
            val code = selectedHymnal?.code ?: selectedCode

            if (hymnsDao.listAll(code).isEmpty()) {
                loadHymns(code)
            }

            prefs.setSelectedHymnal(code)

            val hymnal = getHymnal(code)
            val hymns = hymnsDao.listAll(code).map { it.toHymn() }

            emit(Result.success(HymnalHymns(hymnal, hymns)))

        }.catch {
            Timber.e(it)
            Result.failure<HymnalHymns>(it)
        }.flowOn(dispatcherProvider.io)
    }

    private fun getHymnal(code: String): Hymnal {
        return Hymnals.fromString(code)?.let {
            Hymnal(it.key, it.title, it.language)
        } ?: throw IllegalArgumentException("Invalid Hymnal code")
    }

    private suspend fun loadHymns(code: String) {
        val res = Hymnals.fromString(code)?.resId() ?: return

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

    private fun parseJson(jsonString: String): List<JsonHymn>? {
        val listDataType: Type =
            Types.newParameterizedType(List::class.java, JsonHymn::class.java)
        val adapter: JsonAdapter<List<JsonHymn>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

    override suspend fun searchHymns(query: String?): Result<List<Hymn>> {
        val hymns = hymnsDao.search(selectedCode, "%${query ?: ""}%").map { it.toHymn() }
        return Result.success(hymns)
    }

    override fun updateHymn(hymn: Hymn) {
        launch { hymnsDao.update(hymn.toEntity()) }
    }

    override fun getCollectionHymns(): Flow<Result<List<CollectionHymns>>> = collectionsDao
        .getCollectionsWithHymns()
        .map { entities -> entities.map { it.toModel() } }
        .map { Result.success(it) }
        .catch {
            Timber.e(it)
            Result.failure<List<CollectionHymns>>(it)
        }.flowOn(dispatcherProvider.io)

    override suspend fun searchCollections(query: String?): Result<List<CollectionHymns>> {
        val collections = collectionsDao.searchFor("%${query ?: ""}%")
            .map { it.toModel() }
        return Result.success(collections)
    }

    override fun addCollection(content: TitleBody) {
        val collection =
            HymnCollectionEntity(
                title = content.title,
                description = content.body,
                created = System.currentTimeMillis()
            )
        launch { collectionsDao.insert(collection) }
    }

    override fun updateHymnCollections(hymnId: Int, collectionId: Int, add: Boolean) {
        launch {
            if (add) {
                collectionsDao.insertRef(CollectionHymnCrossRefEntity(collectionId, hymnId))
            } else {
                collectionsDao.findRef(hymnId, collectionId)?.let {
                    collectionsDao.deleteRef(it)
                }
            }
        }
    }

    override suspend fun getCollection(id: Int): Result<CollectionHymns> {
        return collectionsDao.findById(id)?.let {
            Result.success(it.toModel())
        } ?: Result.failure(IllegalArgumentException("Invalid Collection Id"))
    }

    override fun deleteCollection(collection: CollectionHymns) {
        val collectionId = collection.collection.collectionId
        launch {
            collection.hymns.forEach { hymn ->
                collectionsDao.findRef(hymn.hymnId, collectionId)?.let {
                    collectionsDao.deleteRef(it)
                }
            }
            collectionsDao.deleteCollection(collectionId)
        }
    }
}