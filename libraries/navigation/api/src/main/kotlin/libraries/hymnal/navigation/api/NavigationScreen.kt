package libraries.hymnal.navigation.api

import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize
import libraries.hymnal.navigation.model.NavItem

@Parcelize
data class NavigationScreen(
    val selected: NavItem,
): Screen
