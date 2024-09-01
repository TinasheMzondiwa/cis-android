package com.tinashe.hymnal.data.di

import com.tinashe.hymnal.data.model.cfg.DonationsConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HymnalModule {

    @Provides
    fun provideConfig(): DonationsConfig {
        return DonationsConfig(enabled = false)
    }
}
