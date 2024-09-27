package hymnal.search

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

@Immutable
data class State(
    val eventSink: (Event) -> Unit,
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnNavBackClicked : Event
}