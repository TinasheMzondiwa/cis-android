package hymnal.android.intent

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import hymnal.android.sdk.isAtLeastApi

fun <T : Parcelable> Intent.getParcelableCompat(name: String?, clazz: Class<T>): T? {
    return if (isAtLeastApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
        getParcelableExtra(name, clazz)
    } else {
        @Suppress("DEPRECATION") getParcelableExtra(name)
    }
}

fun <T : Parcelable> Bundle.getParcelableCompat(name: String?, clazz: Class<T>): T? {
    return if (isAtLeastApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
        getParcelable(name, clazz)
    } else {
        @Suppress("DEPRECATION") getParcelable(name)
    }
}