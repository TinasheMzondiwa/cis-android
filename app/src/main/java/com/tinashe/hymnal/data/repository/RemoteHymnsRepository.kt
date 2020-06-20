package com.tinashe.hymnal.data.repository

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
import com.tinashe.hymnal.data.model.TitleLanguage
import com.tinashe.hymnal.data.model.json.JsonHymn
import com.tinashe.hymnal.data.model.json.JsonHymnal
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
        private val moshi: Moshi
) {

    private suspend fun checkAuthState() {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }
    }

    suspend fun listHymns(): Resource<List<JsonHymnal>> {
        return try {
            checkAuthState()
            val data: List<JsonHymnal> = suspendCoroutine { continuation ->
                database.getReference("cis")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                throw error.toException()
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val hymns = snapshot.children.mapNotNull { child ->
                                    child.key?.let { code ->
                                        child.getValue<TitleLanguage>()?.let {
                                            JsonHymnal(code, it.title, it.language)
                                        }
                                    }
                                }
                                continuation.resume(hymns)
                            }
                        })
            }
            Resource.success(data)
        } catch (ex: Exception) {
            Timber.e(ex)
            Resource.error(ex)
        }
    }

    suspend fun downloadHymnal(code: String): Resource<List<JsonHymn>?> {
        return try {
            checkAuthState()

            val ref = storage.getReference("cis")
                    .child("$code.$FILE_SUFFIX")
            val localFile = createFile(code)
            val snapshot = ref.getFile(localFile).await()
            if (snapshot.error != null) {
                Timber.e(snapshot.error)
                Resource.error(snapshot.error!!)
            } else {
                val jsonString = Helper.getJson(localFile)
                val hymnal = parseJson(jsonString)
                Resource.success(hymnal)
            }

        } catch (ex: Exception) {
            Timber.e(ex)
            Resource.error(ex)
        }
    }

    private fun createFile(code: String): File = File.createTempFile(code, FILE_SUFFIX)

    private fun parseJson(jsonString: String): List<JsonHymn>? {
        val listDataType: Type = Types.newParameterizedType(List::class.java, JsonHymn::class.java)
        val adapter: JsonAdapter<List<JsonHymn>> = moshi.adapter(listDataType)
        return adapter.fromJson(jsonString)
    }

    companion object {
        private const val FILE_SUFFIX = "json"
    }
}