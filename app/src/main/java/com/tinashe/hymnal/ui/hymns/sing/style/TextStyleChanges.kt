package com.tinashe.hymnal.ui.hymns.sing.style

import androidx.annotation.FontRes
import com.tinashe.hymnal.data.model.constants.UiPref

interface TextStyleChanges {
    fun updateTheme(pref: UiPref)
    fun updateTypeFace(@FontRes fontRes: Int)
    fun updateTextSize(size: Float)
}
