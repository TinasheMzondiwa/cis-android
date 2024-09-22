package app.hymnal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import services.hymnal.prefs.impl.HymnalPrefsImpl

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "hymnal_prefs",
    produceMigrations = {
        listOf(
            SharedPreferencesMigration(
                it,
                "${it.packageName}_preferences",
                setOf(HymnalPrefsImpl.KEY_CODE)
            )
        )
    }
)