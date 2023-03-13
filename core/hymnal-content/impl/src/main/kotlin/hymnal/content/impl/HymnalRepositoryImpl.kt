package hymnal.content.impl

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import hymnal.android.coroutines.DispatcherProvider
import hymnal.content.api.HymnalRepository
import hymnal.content.impl.model.JsonHymn
import hymnal.content.impl.model.JsonHymnal
import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import hymnal.content.model.HymnalHymns
import hymnal.content.model.TitleBody
import hymnal.prefs.HymnalPrefs
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.CollectionHymnCrossRefEntity
import hymnal.storage.entity.HymnCollectionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
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
        val hymnals = readJsonFile<JsonHymnal>("config")?.map {
            Hymnal(it.key, it.title, it.language)
        } ?: return Result.failure(RuntimeException("Could not load hymnals"))

        return Result.success(hymnals)
    }

    override fun getHymns(selectedHymnal: Hymnal?): Flow<Result<HymnalHymns>> {
        val code = selectedHymnal?.code ?: selectedCode
        val hymnal = getHymnal(code)

        prefs.setSelectedHymnal(code)

        return hymnsDao.listAll(code)
            .onEach { if (it.isEmpty()) loadHymns(code) }
            .map { entities -> entities.map { it.toHymn() } }
            .map { hymns ->
                Result.success(HymnalHymns(hymnal, hymns))
            }
            .onStart { emit(Result.success(HymnalHymns(hymnal, emptyList()))) }
            .catch {
                Timber.e(it)
                Result.failure<HymnalHymns>(it)
            }.flowOn(dispatcherProvider.io)
    }

    private fun getHymnal(code: String): Hymnal {
        return getHymnals()
            .getOrDefault(emptyList())
            .firstOrNull { it.code == code }
            ?: throw IllegalArgumentException("Invalid Hymnal code")
    }

    private fun loadHymns(code: String) = launch {
        val hymns = readJsonFile<JsonHymn>(code) ?: return@launch
        hymnsDao.insertAll(hymns.map { it.toHymnEntity(code) })
    }

    private inline fun <reified T> readJsonFile(assetFile: String): List<T>? {
        val jsonString = context.assets.open("cis-hymnals/$assetFile.json")
            .bufferedReader()
            .use { it.readText() }

        val listDataType: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listDataType)
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
