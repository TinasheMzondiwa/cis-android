package app.hymnal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import dagger.hilt.components.SingletonComponent
import libraries.hymnal.navigation.api.NavigationScreen
import libraries.hymnal.navigation.model.NavItem

@CircuitInject(HomeScreen::class, SingletonComponent::class)
@Composable
fun HomeScreenUi(state: HomeState, modifier: Modifier = Modifier) {
    println(state)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            CircuitContent(NavigationScreen(NavItem.HYMNS))
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues))
    }
}