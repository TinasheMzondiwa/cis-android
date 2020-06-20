package com.tinashe.hymnal.extensions.prefs

interface HymnalPrefs {

    fun getSelectedHymnal(): String

    fun setSelectedHymnal(code: String)
}