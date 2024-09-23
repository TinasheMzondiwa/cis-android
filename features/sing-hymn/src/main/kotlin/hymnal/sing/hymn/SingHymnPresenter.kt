package hymnal.sing.hymn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import hymnal.prefs.HymnalPrefs
import hymnal.sing.hymn.model.HymnContent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import libraries.hymnal.navigation.api.SingHymnScreen

class SingHymnPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: SingHymnScreen,
    private val prefs: HymnalPrefs,
    private val repository: HymnalRepository
) : Presenter<State> {

    @CircuitInject(SingHymnScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: SingHymnScreen): SingHymnPresenter
    }

    @Composable
    override fun present(): State {
        val displayPrefs by rememberRetained {
            mutableStateOf(DisplayPrefs(prefs.getFontRes(), prefs.getFontSize()))
        }
        val titleHymns by observeHymns()
        val (title, hymns) = titleHymns

        return State(
            title = title,
            content = hymns.map { it.content() }.toImmutableList(),
            currentPage = hymns.indexOfFirst { it.hymnId == screen.hymnId },
            displayPrefs = displayPrefs,
        ) { event ->
            when (event) {
                Event.OnBackClicked -> navigator.pop()
            }
        }
    }

    @Composable
    private fun observeHymns() = produceRetainedState(initialValue = "" to emptyList()) {
        when {
            screen.collectionId != null -> {
                val collection = repository.getCollection(screen.collectionId!!).getOrNull()
                    ?: return@produceRetainedState
                value = collection.collection.title to collection.hymns
            }

            else -> {
                repository.getHymns()
                    .map { it.getOrNull() }
                    .filterNotNull()
                    .collect { hymns ->
                        value = hymns.title to hymns.hymns
                    }
            }
        }
    }

    private fun Hymn.content(): HymnContent {
        return when {
            editedContent?.isNotEmpty() == true -> HymnContent.Html(editedContent.orEmpty())
            markdown != null -> HymnContent.Markdown(markdown.orEmpty())
            else -> HymnContent.Html(html.orEmpty())
        }
    }
}