package com.tinashe.hymnal.data.model

import android.os.Parcelable
import androidx.annotation.FontRes
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.UiPref
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextStyleModel(
    val pref: UiPref = UiPref.FOLLOW_SYSTEM,
    @FontRes val fontRes: Int = R.font.proxima_nova_soft_medium,
    val textSize: Float = 18f
) : Parcelable
