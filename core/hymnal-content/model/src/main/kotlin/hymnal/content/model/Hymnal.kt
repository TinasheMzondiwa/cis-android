package hymnal.content.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hymnal(
    val code: String,
    val title: String,
    val language: String
) : Parcelable
