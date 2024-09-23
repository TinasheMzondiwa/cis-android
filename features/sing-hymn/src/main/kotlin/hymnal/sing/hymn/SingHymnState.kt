package hymnal.sing.hymn

import androidx.annotation.FontRes
import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiState
import hymnal.sing.hymn.model.HymnContent
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class State(
    val title: String,
    val content: ImmutableList<HymnContent>,
    val currentPage: Int,
    val displayPrefs: DisplayPrefs,
    val eventSink: (Event) -> Unit,
): CircuitUiState

sealed interface Event {
    data object OnBackClicked : Event
}

@Immutable
data class DisplayPrefs(
    @FontRes val fontRes: Int,
    val fontSize: Float
)