package hymnal.content.impl

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import hymnal.android.coroutines.DispatcherProvider
import hymnal.android.coroutines.Scopable
import hymnal.android.coroutines.ioScopable
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
) : HymnalRepository, Scopable by ioScopable(dispatcherProvider) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    override fun getHymnals(): Result<List<Hymnal>> {
        val hymnals = readJsonFile<JsonHymnal>("config")?.map {
            Hymnal(it.key, it.title, it.language)
        } ?: return Result.failure(RuntimeException("Could not load hymnals"))

        return Result.success(hymnals)
    }

    override fun getHymns(): Flow<Result<HymnalHymns>> = prefs.getSelected()
        .flatMapLatest { code ->
            hymnsDao.listAll(code)
                .map { getHymnal(code) to it }
        }
        .map { (hymnal, entities) ->
            val hymns = entities.map { it.toHymn() }
            if (entities.isEmpty()) {
                loadHymns(hymnal.code)
            }
            Result.success(HymnalHymns(hymnal, hymns))
        }
        .catch {
            Timber.e(it)
            Result.failure<HymnalHymns>(it)
        }.flowOn(dispatcherProvider.io)

    override fun setSelectedHymnal(hymnal: Hymnal) {
        prefs.setSelectedHymnal(hymnal.code)
    }

    private fun getHymnal(code: String): Hymnal {
        return getHymnals()
            .getOrDefault(emptyList())
            .firstOrNull { it.code == code }
            ?: throw IllegalArgumentException("Invalid Hymnal code => $code")
    }

    private fun loadHymns(code: String) = scope.launch {
        val hymns = readJsonFile<JsonHymn>(code) ?: return@launch
        hymnsDao.insertAll(hymns.map { it.toHymnEntity(code) })
    }

    private inline fun <reified T> readJsonFile(assetFile: String): List<T>? {
        val jsonString = context.assets.open("$assetFile.json")
            .bufferedReader()
            .use { it.readText() }

        val listDataType: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

    override suspend fun searchHymns(query: String?): Result<List<Hymn>> {
        val entities = withContext(dispatcherProvider.io) {
            hymnsDao.search(selectedCode, "%${query ?: ""}%")
        }
        return withContext(dispatcherProvider.default) {
            Result.success(entities.map { it.toHymn() })
        }
    }

    override fun updateHymn(hymn: Hymn) {
        scope.launch { hymnsDao.update(hymn.toEntity()) }
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
        scope.launch { collectionsDao.insert(collection) }
    }

    override fun updateHymnCollections(hymnId: Int, collectionId: Int, add: Boolean) {
        scope.launch {
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
        scope.launch {
            collection.hymns.forEach { hymn ->
                collectionsDao.findRef(hymn.hymnId, collectionId)?.let {
                    collectionsDao.deleteRef(it)
                }
            }
            collectionsDao.deleteCollection(collectionId)
        }
    }
}
