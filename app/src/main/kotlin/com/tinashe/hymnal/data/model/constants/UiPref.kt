package com.tinashe.hymnal.data.model.constants

import androidx.appcompat.app.AppCompatDelegate

enum class UiPref(val value: String) {
    DAY("day"),
    NIGHT("night"),
    BATTERY_SAVER("battery_saver"),
    FOLLOW_SYSTEM("follow_system");

    companion object {
        private val map = values().associateBy(UiPref::value)

        fun fromString(type: String) = map[type]
    }
}

fun UiPref.setDefaultNightMode() {
    val theme = when (this) {
        UiPref.DAY -> AppCompatDelegate.MODE_NIGHT_NO
        UiPref.NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
        UiPref.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        UiPref.BATTERY_SAVER -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    }

    AppCompatDelegate.setDefaultNightMode(theme)
}
