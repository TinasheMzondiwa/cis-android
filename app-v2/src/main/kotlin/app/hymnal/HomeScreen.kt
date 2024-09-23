package app.hymnal

import androidx.compose.runtime.Immutable
import app.hymnal.model.NavItemModel
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
object HomeScreen : Screen

@Immutable
data class HomeState(
    val currentNavItem: NavItemModel,
    val currentScreen: Screen,
    val navEntries: ImmutableList<NavItemModel>,
    val eventSink: (Event) -> Unit
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data class OnItemSelected(val item: NavItemModel) : Event
    data class OnNavEvent(val navEvent: NavEvent) : Event
}