package hymnal.hymns

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
import hymnal.content.model.HymnalHymns
import kotlinx.collections.immutable.toImmutableList
import libraries.hymnal.hymns.ui.model.HymnModel
import libraries.hymnal.navigation.api.HymnsScreen
import libraries.hymnal.navigation.api.SearchScreen
import libraries.hymnal.navigation.api.SingHymnScreen

class HymnsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val repository: HymnalRepository
) : Presenter<State> {

    @CircuitInject(HymnsScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HymnsPresenter
    }

    @Composable
    override fun present(): State {
        var sortType by rememberRetained { mutableStateOf(SortType.TITLE) }
        val hymnalHymns by produceRetainedState<HymnalHymns?>(initialValue = null) {
            repository.getHymns().collect { hymns -> value = hymns.getOrNull() }
        }
        val title by rememberRetained(hymnalHymns) { mutableStateOf(hymnalHymns?.title) }
        val hymns by rememberRetained(hymnalHymns, sortType) {
            mutableStateOf(
                hymnalHymns?.hymns?.map {
                    HymnModel(it.hymnId, it.title.display(sortType, it.number), it.number)
                }?.sorted(sortType)
            )
        }

        val hymnTitle = title

        return when {
            hymnTitle == null -> State.Loading
            else -> State.Content(
                sortType,
                hymnTitle,
                (hymns ?: emptyList()).toImmutableList()
            ) { event ->
                when (event) {
                    is Event.OnHymnClicked -> navigator.goTo(SingHymnScreen(event.hymn.id))

                    Event.OnSortClicked -> {
                        sortType = sortType.next()
                    }

                    Event.OnSearchClicked -> navigator.goTo(SearchScreen)
                }
            }
        }
    }

    private fun List<HymnModel>.sorted(sortType: SortType): List<HymnModel> {
        return when (sortType.next()) {
            SortType.NUMBER -> sortedBy { it.number }
            SortType.TITLE -> sortedBy { it.title }
        }
    }

    private fun String.display(sortType: SortType, number: Int) = when (sortType.next()) {
        SortType.NUMBER -> this
        SortType.TITLE -> "${replace(Regex("^\\d+\\s*"), "")} - $number"
    }
}