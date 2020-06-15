package com.tinashe.hymnal.data.repository

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.json.JsonHymnal
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
    private val hymnsDao: HymnsDao
) {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun hymnsList(): Flow<List<Hymn>> = flow {
        val hymns = hymnalsDao.getHymns(DEF_CODE)
        if (hymns != null) {
            emit(hymns.hymns)
            return@flow
        }

        val sample = getSample()
        sample?.let { hymnal ->
            hymnalsDao.insert(Hymnal(hymnal.key, hymnal.title, hymnal.language))
            hymnsDao.insertAll(hymnal.hymns.map { it.toHymn(hymnal.key) })
        }

        val data = hymnalsDao.getHymns(DEF_CODE)?.hymns ?: emptyList<Hymn>()
        emit(data)
    }.catch {
        Timber.e(it)
    }.flowOn(Dispatchers.IO)

    private fun getSample(): JsonHymnal? {
        val jsonString = Helper.getJson(context.resources, R.raw.sample)
        val adapter: JsonAdapter<JsonHymnal> = moshi.adapter(JsonHymnal::class.java)
        return adapter.fromJson(jsonString)
    }

    companion object {
        private const val DEF_CODE = "english"
    }
}