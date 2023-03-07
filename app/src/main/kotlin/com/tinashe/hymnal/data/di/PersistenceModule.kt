package com.tinashe.hymnal.data.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.extensions.prefs.HymnalPrefsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    fun provideHymnalPrefs(context: Context): HymnalPrefs = HymnalPrefsImpl(
        PreferenceManager.getDefaultSharedPreferences(context)
    )
}
