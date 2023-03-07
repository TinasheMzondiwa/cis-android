package com.tinashe.hymnal.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.repository.HymnalRepository
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
        moshi: Moshi,
        hymnsDao: HymnsDao,
        collectionsDao: CollectionsDao,
        hymnalPrefs: HymnalPrefs
    ): HymnalRepository = HymnalRepository(
        context,
        moshi,
        hymnsDao,
        collectionsDao,
        hymnalPrefs
    )
}
