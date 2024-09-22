package hymnal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import libraries.hymnal.navigation.api.NavigationScreen

class NavigationPresenter @AssistedInject constructor(
    @Assisted private val screen: NavigationScreen,
) : Presenter<State> {

    @CircuitInject(NavigationScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(screen: NavigationScreen): NavigationPresenter
    }

    @Composable
    override fun present(): State {
        var selectedItem by rememberRetained { mutableStateOf(screen.selected) }

        return State(
            selected = selectedItem,
        ) { event ->
            when (event) {
                is Event.OnItemSelected -> {
                    selectedItem = event.item
                }
            }
        }
    }
}