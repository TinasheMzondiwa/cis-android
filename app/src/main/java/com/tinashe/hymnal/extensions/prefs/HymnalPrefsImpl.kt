package com.tinashe.hymnal.extensions.prefs

import android.content.SharedPreferences
import androidx.annotation.FontRes
import androidx.core.content.edit
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.TextStyleModel
import com.tinashe.hymnal.data.model.constants.UiPref

class HymnalPrefsImpl(private val prefs: SharedPreferences) : HymnalPrefs {

    override fun getSelectedHymnal(): String = prefs.getString(KEY_CODE, DEFAULT_CODE)!!

    override fun setSelectedHymnal(code: String) {
        prefs.edit { putString(KEY_CODE, code) }
    }

    override fun getUiPref(): UiPref {
        val value = prefs.getString(KEY_UI_PREF, null) ?: return UiPref.FOLLOW_SYSTEM
        return UiPref.fromString(value) ?: UiPref.FOLLOW_SYSTEM
    }

    override fun setUiPref(pref: UiPref) {
        prefs.edit {
            putString(KEY_UI_PREF, pref.value)
        }
    }

    override fun setFontRes(@FontRes fontRes: Int) {
        prefs.edit { putInt(KEY_FONT_STYLE, fontRes) }
    }

    override fun getFontRes(): Int = prefs.getInt(KEY_FONT_STYLE, R.font.proxima_nova_soft_regular)

    override fun setFontSize(size: Float) {
        prefs.edit { putFloat(KEY_FONT_SIZE, size) }
    }

    override fun getFontSize(): Float = prefs.getFloat(KEY_FONT_SIZE, 22f)

    override fun getTextStyleModel(): TextStyleModel = TextStyleModel(
        getUiPref(),
        getFontRes(),
        getFontSize()
    )

    override fun isHymnalPromptSeen(): Boolean = prefs.getBoolean(KEY_HYMNALS_PROMPT, false)

    override fun setHymnalPromptSeen() {
        prefs.edit {
            putBoolean(KEY_HYMNALS_PROMPT, true)
        }
    }

    companion object {
        private const val DEFAULT_CODE = "english"

        private const val KEY_CODE = "pref:default_code"
        private const val KEY_UI_PREF = "pref:app_theme"
        private const val KEY_FONT_STYLE = "pref:font_res"
        private const val KEY_FONT_SIZE = "pref:font_size"
        private const val KEY_HYMNALS_PROMPT = "pref:hymnals_list_prompt"
    }
}
