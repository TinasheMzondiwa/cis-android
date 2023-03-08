package hymnal.prefs

import androidx.annotation.FontRes
import hymnal.prefs.model.TextStyleModel
import hymnal.prefs.model.HymnalSort
import hymnal.prefs.model.UiPref

interface HymnalPrefs {

    fun getSelectedHymnal(): String

    fun setSelectedHymnal(code: String)

    fun getUiPref(): UiPref

    fun setUiPref(pref: UiPref)

    fun setFontRes(@FontRes fontRes: Int)

    @FontRes
    fun getFontRes(): Int

    fun setFontSize(size: Float)

    fun getFontSize(): Float

    fun getTextStyleModel(): TextStyleModel

    fun isHymnalPromptSeen(): Boolean

    fun setHymnalPromptSeen()

    fun getHymnalSort(): HymnalSort

    fun setHymnalSort(sort: HymnalSort)
}
