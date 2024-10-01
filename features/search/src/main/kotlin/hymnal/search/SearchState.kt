package hymnal.search

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import kotlinx.collections.immutable.ImmutableList
import libraries.hymnal.hymns.ui.model.HymnModel

@Immutable
data class State(
    val results: ImmutableList<HymnModel>,
    val eventSink: (Event) -> Unit,
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnNavBackClicked : Event
    data object OnQueryCleared : Event
    data class OnQueryChanged(val query: String) : Event
    data class OnHymnClicked(val hymn: HymnModel) : Event
}