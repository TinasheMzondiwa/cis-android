package hymnal.sing.hymn.model

import androidx.compose.runtime.Stable

@Stable
sealed interface HymnContent {

    val content: String

    data class Html(override val content: String) : HymnContent

    data class Markdown(override val content: String) : HymnContent

}