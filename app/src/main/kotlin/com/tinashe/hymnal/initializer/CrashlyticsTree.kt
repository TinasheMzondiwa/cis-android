package com.tinashe.hymnal.initializer

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }
        FirebaseCrashlytics.getInstance().log(message)
        t?.let {
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }
}
