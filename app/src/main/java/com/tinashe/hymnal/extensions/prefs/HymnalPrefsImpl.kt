package com.tinashe.hymnal.extensions.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class HymnalPrefsImpl(private val prefs: SharedPreferences) : HymnalPrefs {

    override fun getSelectedHymnal(): String = prefs.getString(KEY_CODE, DEFAULT_CODE)!!

    override fun setSelectedHymnal(code: String) {
        prefs.edit { putString(KEY_CODE, code) }
    }

    companion object {
        private const val DEFAULT_CODE = "english"

        private const val KEY_CODE = "pref:default_code"
    }
}