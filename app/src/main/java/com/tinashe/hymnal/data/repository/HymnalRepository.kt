package com.tinashe.hymnal.data.repository

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.HymnalHymns
import com.tinashe.hymnal.data.model.remote.RemoteHymnal
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class HymnalRepository(
        private val context: Context,
        private val hymnalsDao: HymnalsDao,
        private val hymnsDao: HymnsDao,
        private val moshi: Moshi,
        private val prefs: HymnalPrefs,
        private val remoteHymnsRepository: RemoteHymnsRepository
) {

    private val selectedCode: String get() = prefs.getSelectedHymnal()

    fun getHymns(selectedHymnal: RemoteHymnal? = null) = flow {
        val hymnal = hymnalsDao.findByCode(selectedHymnal?.key ?: selectedCode)

        when {
            hymnal == null && hymnalsDao.findByCode(selectedCode) == null -> {
                emit(Resource.loading())

                val sample = getSample()
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
                        selectedHymnal.key)

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

        val data = hymnalsDao.findByCode(selectedCode)
        val hymns = hymnsDao.listAll(selectedCode)
        data?.let {
            emit(Resource.success(HymnalHymns(data, hymns)))
        }

    }.catch {
        Timber.e(it)
        emit(Resource.error(it))
    }.flowOn(Dispatchers.IO)

    private fun getSample(): RemoteHymnal? {
        val jsonString = Helper.getJson(context.resources, R.raw.english)
        val adapter: JsonAdapter<RemoteHymnal> = moshi.adapter(RemoteHymnal::class.java)
        return adapter.fromJson(jsonString)
    }

    suspend fun searchHymns(query: String?): List<Hymn> {
        return hymnsDao.search(selectedCode, "%${query ?: ""}%")
    }
}