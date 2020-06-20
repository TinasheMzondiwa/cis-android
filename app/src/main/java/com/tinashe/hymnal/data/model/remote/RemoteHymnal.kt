package com.tinashe.hymnal.data.model.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RemoteHymnal(
    val key: String,
    val title: String,
    val language: String,
    val hymns: List<RemoteHymn> = emptyList()
) : Parcelable
