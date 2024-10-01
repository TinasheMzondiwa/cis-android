package libraries.hymnal.hymns.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class HymnModel(
    val id: Int,
    val title: String,
    val number: Int,
)
