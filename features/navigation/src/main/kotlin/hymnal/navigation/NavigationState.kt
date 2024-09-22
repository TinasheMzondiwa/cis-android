package hymnal.navigation

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import libraries.hymnal.navigation.model.NavItem

@Immutable
data class State(
    val selected: NavItem,
    val eventSink: (Event) -> Unit
): CircuitUiState

sealed interface Event : CircuitUiEvent {
    data class OnItemSelected(val item: NavItem) : Event
}