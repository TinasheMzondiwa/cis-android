package com.tinashe.hymnal.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tinashe.hymnal.BuildConfig
import timber.log.Timber

class TimberInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // Timber.plant(CrashlyticsTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}