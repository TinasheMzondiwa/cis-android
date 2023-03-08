package hymnal.content.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hymn(
    val hymnId: Int,
    val book: String,
    val number: Int,
    val title: String,
    val content: String,
    val majorKey: String?,
    var editedContent: String?
) : Parcelable
