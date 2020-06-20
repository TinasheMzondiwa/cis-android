package com.tinashe.hymnal.data.model.json

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JsonHymnal(
        val key: String,
        val title: String,
        val language: String,
        val hymns: List<JsonHymn> = emptyList()
) : Parcelable
