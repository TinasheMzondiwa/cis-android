package com.tinashe.hymnal.extensions.connectivity

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

val ConnectivityManager.isConnected: Boolean
    get() = isConnectedToWifi || isConnectedToCellular

val ConnectivityManager.isConnectedToWifi: Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val info = getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            info?.isConnected ?: false
        }
    }

val ConnectivityManager.isConnectedToCellular: Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val info = getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            info?.isConnected ?: false
        }
    }
