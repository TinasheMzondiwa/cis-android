package com.tinashe.hymnal.data.di

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.data.model.cfg.DonationsConfig
import com.tinashe.hymnal.extensions.prefs.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hymnal.android.coroutines.DispatcherProvider
import hymnal.prefs.HymnalPrefs
import libraries.hymnal.models.AppConfig
import services.hymnal.prefs.impl.HymnalPrefsImpl

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

    @Provides
    fun provideAppConfig(): AppConfig {
        return AppConfig(
            packageName = BuildConfig.APPLICATION_ID,
            version = BuildConfig.VERSION_NAME,
            isDebug = BuildConfig.DEBUG
        )
    }

    @Provides
    fun providePrefs(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider,
    ): HymnalPrefs = HymnalPrefsImpl(
        context = context,
        dataStore = context.dataStore,
        dispatcherProvider = dispatcherProvider,
    )
}
