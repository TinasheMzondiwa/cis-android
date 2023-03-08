package com.tinashe.hymnal.data.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tinashe.hymnal.data.model.cfg.DonationsConfig
import com.tinashe.hymnal.extensions.prefs.HymnalPrefsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hymnal.prefs.HymnalPrefs

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
    fun provideHymnalPrefs(
        @ApplicationContext context: Context
    ): HymnalPrefs = HymnalPrefsImpl(
        PreferenceManager.getDefaultSharedPreferences(context)
    )
}
