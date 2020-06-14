package com.tinashe.hymnal.data.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object HymnalModule {

    @Provides
    fun provideContext(app: Application): Context = app
}