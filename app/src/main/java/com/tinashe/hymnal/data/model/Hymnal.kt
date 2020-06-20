package com.tinashe.hymnal.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "hymnals")
@Parcelize
data class Hymnal(
    @PrimaryKey
    val code: String,
    val title: String,
    val language: String
): Parcelable