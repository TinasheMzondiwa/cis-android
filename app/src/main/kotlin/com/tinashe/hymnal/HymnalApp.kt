package com.tinashe.hymnal

import android.app.Application
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.Helper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HymnalApp : Application() {

    @Inject
    lateinit var prefs: HymnalPrefs

    override fun onCreate() {
        super.onCreate()

        Helper.switchToTheme(prefs.getUiPref())
    }
}
