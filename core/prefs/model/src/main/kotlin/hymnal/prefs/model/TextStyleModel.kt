package hymnal.prefs.model

import android.os.Parcelable
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextStyleModel(
    val pref: UiPref,
    @FontRes val fontRes: Int,
    val textSize: Float
) : Parcelable
