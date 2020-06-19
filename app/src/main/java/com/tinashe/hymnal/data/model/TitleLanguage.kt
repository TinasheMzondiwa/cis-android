package com.tinashe.hymnal.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TitleLanguage(val title: String, val language: String) {
    constructor(): this("", "")
}