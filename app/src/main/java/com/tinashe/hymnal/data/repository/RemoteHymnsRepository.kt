package com.tinashe.hymnal.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.TitleLanguage
import com.tinashe.hymnal.data.model.remote.RemoteHymn
import com.tinashe.hymnal.data.model.remote.RemoteHymnal
import com.tinashe.hymnal.data.model.remote.RemoteHymnalJsonAdapter
import com.tinashe.hymnal.data.model.response.Resource
import com.tinashe.hymnal.utils.Helper
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteHymnsRepository(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val moshi: Moshi,
    private val context: Context
) {

    fun getSample(): RemoteHymnal? {
        val jsonString = Helper.getJson(context.resources, R.raw.english)
        val adapter: JsonAdapter<RemoteHymnal> = RemoteHymnalJsonAdapter(moshi)
        return adapter.fromJson(jsonString)
    }

    private suspend fun checkAuthState() {
        if (auth.currentUser == null) {
            val result = auth.signInAnonymously().await()
            if (result.user == null) {
                throw IllegalStateException("Could not authenticate user")
            }
        }
    }

    suspend fun listHymnals(): Resource<List<RemoteHymnal>> {
        return try {
            checkAuthState()
            val data: List<RemoteHymnal> = suspendCoroutine { continuation ->
                database.getReference(FOLDER)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            throw error.toException()
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val hymnals = snapshot.children.mapNotNull { child ->
                                child.key?.let { code ->
                                    child.getValue<TitleLanguage>()?.let {
                                        RemoteHymnal(code, it.title, it.language)
                                    }
                                }
                            }
                            continuation.resume(hymnals)
                        }
                    })
            }
            Resource.success(data)
        } catch (ex: Exception) {
            Timber.e(ex)
            Resource.error(ex)
        }
    }

    suspend fun downloadHymns(code: String): Resource<List<RemoteHymn>?> {
        return try {
            checkAuthState()

            val ref = storage.getReference(FOLDER)
                .child("$code.$FILE_SUFFIX")
            val localFile = createFile(code)
            val snapshot = ref.getFile(localFile).await()
            if (snapshot.error != null) {
                Timber.e(snapshot.error)
                Resource.error(snapshot.error!!)
            } else {
                val jsonString = Helper.getJson(localFile)
                val hymns = parseJson(jsonString)
                Resource.success(hymns)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            Resource.error(ex)
        }
    }

    private fun createFile(code: String): File = File.createTempFile(code, FILE_SUFFIX)

    private fun parseJson(jsonString: String): List<RemoteHymn>? {
        val listDataType: Type =
            Types.newParameterizedType(List::class.java, RemoteHymn::class.java)
        val adapter: JsonAdapter<List<RemoteHymn>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

    companion object {
        private const val FOLDER = "cis"
        private const val FILE_SUFFIX = "json"
    }
}
