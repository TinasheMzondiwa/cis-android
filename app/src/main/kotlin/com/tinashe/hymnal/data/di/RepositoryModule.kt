package com.tinashe.hymnal.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.repository.HymnalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import hymnal.content.api.HymnalRepository
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao

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
    ): HymnalRepository = HymnalRepositoryImpl(
        context,
        moshi,
        hymnsDao,
        collectionsDao,
        hymnalPrefs
    )
}
