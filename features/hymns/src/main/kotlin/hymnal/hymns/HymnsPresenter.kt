package hymnal.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import hymnal.content.api.HymnalRepository
import hymnal.content.model.HymnalHymns
import hymnal.hymns.model.HymnModel
import kotlinx.collections.immutable.toImmutableList
import libraries.hymnal.navigation.api.HymnsScreen

class HymnsPresenter @AssistedInject constructor(
    private val repository: HymnalRepository
) : Presenter<State> {

    @CircuitInject(HymnsScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(): HymnsPresenter
    }

    @Composable
    override fun present(): State {
        val hymnalHymns by produceRetainedState<HymnalHymns?>(initialValue = null) {
            repository.getHymns()
                .collect { hymns ->
                    value = hymns.getOrNull()
                }

        }
        val title by rememberRetained(hymnalHymns) { mutableStateOf(hymnalHymns?.title) }
        val hymns by rememberRetained(hymnalHymns) {
            mutableStateOf(hymnalHymns?.hymns?.map { HymnModel(it.hymnId, it.title, it.number) })
        }

        val hymnTitle = title

        return when {
            hymnTitle == null -> State.Loading
            else -> State.Content(hymnTitle, (hymns ?: emptyList()).toImmutableList())
        }

    }
}