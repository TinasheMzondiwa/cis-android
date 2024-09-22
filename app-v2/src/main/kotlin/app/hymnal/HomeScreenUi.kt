package app.hymnal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import dagger.hilt.components.SingletonComponent

@CircuitInject(HomeScreen::class, SingletonComponent::class)
@Composable
fun HomeScreenUi(state: HomeState, modifier: Modifier = Modifier) {
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            state.navEntries.forEach { model ->
                item(
                    icon = {
                        Icon(
                            imageVector = model.icon,
                            contentDescription = stringResource(model.title),
                        )
                    },
                    label = {
                        Text(text = stringResource(model.title))
                    },
                    selected = state.currentNavItem == model,
                    onClick = { state.eventSink(Event.OnItemSelected(model)) },
                )
            }
        },
        modifier = modifier,
    ) {
        CircuitContent(
            screen = state.currentScreen,
            modifier = Modifier.fillMaxSize(),
            onNavEvent = { state.eventSink(Event.OnNavEvent(it)) },
        )
    }
}