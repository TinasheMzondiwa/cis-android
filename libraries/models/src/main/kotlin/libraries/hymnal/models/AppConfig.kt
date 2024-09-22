package libraries.hymnal.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppConfig(
    val packageName: String,
    val version: String,
    val isDebug: Boolean = false
): Parcelable
