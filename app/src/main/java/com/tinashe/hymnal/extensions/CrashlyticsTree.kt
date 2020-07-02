package com.tinashe.hymnal.extensions

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }
        FirebaseCrashlytics.getInstance().log(message)
        throwable?.let {
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }
}
