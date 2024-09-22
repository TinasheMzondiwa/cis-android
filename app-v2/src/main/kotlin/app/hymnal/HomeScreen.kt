package app.hymnal

import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object HomeScreen : Screen

data object HomeState : CircuitUiState