package com.tinashe.hymnal.data.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
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
        val config = Firebase.remoteConfig.also {
            it.fetchAndActivate()
        }
        return DonationsConfig(
            enabled = config.getBoolean("cfg_donations_enabled")
        )
    }
}
