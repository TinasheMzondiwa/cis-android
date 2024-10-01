package hymnal.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import hymnal.content.api.HymnalRepository
import hymnal.content.model.Hymn
import kotlinx.collections.immutable.toImmutableList
import libraries.hymnal.hymns.ui.model.HymnModel
import libraries.hymnal.navigation.api.SearchScreen
import libraries.hymnal.navigation.api.SingHymnScreen

class SearchPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val repository: HymnalRepository,
) : Presenter<State> {

    @CircuitInject(SearchScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SearchPresenter
    }

    @Composable
    override fun present(): State {
        var query by rememberRetained { mutableStateOf<String?>(null) }
        val results by produceRetainedState<List<Hymn>>(emptyList(), query) {
            value = if (query.isNullOrEmpty()) {
                emptyList()
            } else {
                repository.searchHymns(query)
                    .getOrDefault(emptyList())
            }
        }
        return State(
            results = results.map {
                HymnModel(it.hymnId, it.title, it.number)
            }.toImmutableList()
        ) { event ->
            when (event) {
                is Event.OnNavBackClicked -> navigator.pop()
                is Event.OnQueryChanged -> query = event.query.trim()
                Event.OnQueryCleared -> query = null
                is Event.OnHymnClicked -> navigator.goTo(SingHymnScreen(event.hymn.id))
            }
        }
    }
}