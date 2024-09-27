package hymnal.search

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import libraries.hymnal.navigation.api.SearchScreen

class SearchPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
): Presenter<State> {

    @CircuitInject(SearchScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SearchPresenter
    }
    @Composable
    override fun present(): State {
        return State { event ->
            when (event) {
                is Event.OnNavBackClicked -> navigator.pop()
            }
        }
    }
}