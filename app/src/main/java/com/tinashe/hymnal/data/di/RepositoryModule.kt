package com.tinashe.hymnal.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.repository.HymnalRepository
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
        moshi: Moshi
    ): HymnalRepository = HymnalRepository(context, hymnalsDao, hymnsDao, moshi)
}