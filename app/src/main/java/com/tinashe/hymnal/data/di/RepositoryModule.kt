package com.tinashe.hymnal.data.di

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.data.repository.RemoteHymnsRepository
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRepository(
        context: Context,
        hymnalsDao: HymnalsDao,
        hymnsDao: HymnsDao,
        moshi: Moshi,
        hymnalPrefs: HymnalPrefs
    ): HymnalRepository = HymnalRepository(context, hymnalsDao, hymnsDao, moshi, hymnalPrefs)

    @Provides
    @ActivityRetainedScoped
    fun provideRemoteRepository(): RemoteHymnsRepository = RemoteHymnsRepository(
        Firebase.database,
        Firebase.auth,
        Firebase.storage
    )
}