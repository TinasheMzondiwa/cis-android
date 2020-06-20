package com.tinashe.hymnal.data.repository

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.json.JsonHymnal
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class HymnalRepository(
    private val context: Context,
    private val hymnalsDao: HymnalsDao,
    private val hymnsDao: HymnsDao,
    private val moshi: Moshi,
    private val prefs: HymnalPrefs
) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    fun hymnsList(): Flow<Resource<List<Hymn>>> = flow {
        val hymns = hymnsDao.listAll(selectedCode)
        if (hymns.isNotEmpty()) {
            emit(Resource.success(hymns))
            return@flow
        }

        emit(Resource.loading())

        val sample = getSample()
        sample?.let { hymnal ->
            hymnalsDao.insert(Hymnal(hymnal.key, hymnal.title, hymnal.language))
            hymnsDao.insertAll(hymnal.hymns.map { it.toHymn(hymnal.key) })
        }

        val data: List<Hymn> = hymnsDao.listAll(selectedCode)
        emit(Resource.success(data))
    }.catch {
        Timber.e(it)
        emit(Resource.error(it))
    }.flowOn(Dispatchers.IO)

    private fun getSample(): JsonHymnal? {
        val jsonString = Helper.getJson(context.resources, R.raw.sample)
        val adapter: JsonAdapter<JsonHymnal> = moshi.adapter(JsonHymnal::class.java)
        return adapter.fromJson(jsonString)
    }

    suspend fun searchHymns(query: String?): List<Hymn> {
        return hymnsDao.search(selectedCode, "%${query ?: ""}%")
    }
}