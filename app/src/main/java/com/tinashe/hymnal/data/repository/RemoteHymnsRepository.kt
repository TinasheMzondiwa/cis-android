package com.tinashe.hymnal.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.TitleLanguage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RemoteHymnsRepository(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) {

    private suspend fun checkAuthState() {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }
    }

    suspend fun listHymns(): List<Hymnal> {
        checkAuthState()

        return suspendCoroutine { continuation ->
            database.getReference("cis")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val hymns = snapshot.children.mapNotNull { child ->
                            child.key?.let { code ->
                                child.getValue<TitleLanguage>()?.let {
                                    Hymnal(code, it.title, it.language)
                                }
                            }
                        }
                        continuation.resume(hymns)
                    }
                })
        }
    }
}