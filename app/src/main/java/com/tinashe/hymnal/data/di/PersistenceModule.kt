package com.tinashe.hymnal.data.di

import android.content.Context
import androidx.room.Room
import com.tinashe.hymnal.data.db.HymnalDatabase
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object PersistenceModule {

    @Provides
    fun provideDatabase(context: Context): HymnalDatabase =
        Room.databaseBuilder(context, HymnalDatabase::class.java, "hymnal.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHymnalsDao(database: HymnalDatabase): HymnalsDao = database.hymnalsDao()

    @Provides
    fun provideHymnsDao(database: HymnalDatabase): HymnsDao = database.hymnsDao()
}