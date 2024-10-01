package hymnal.search

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import kotlinx.collections.immutable.persistentListOf
import libraries.hymnal.hymns.ui.HymnsList
import libraries.hymnal.navigation.api.SearchScreen
import libraries.hymnal.ui.HymnalTheme
import hymnal.l10n.R as L10nR

@CircuitInject(SearchScreen::class, SingletonComponent::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenUi(state: State, modifier: Modifier = Modifier) {
    val textFieldState = rememberTextFieldState()

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = {
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                    state.eventSink(Event.OnQueryChanged(it))
                },
                onSearch = { },
                expanded = true,
                onExpandedChange = { },
                placeholder = { Text(stringResource(L10nR.string.hint_search_hymns)) },
                leadingIcon = {
                    IconButton(onClick = { state.eventSink(Event.OnNavBackClicked) }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(L10nR.string.navigation_back)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textFieldState.clearText()
                            state.eventSink(Event.OnQueryCleared)
                        }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                }
            )
        },
        expanded = true,
        onExpandedChange = { },
    ) {
        HymnsList(
            hymns = state.results,
            modifier = Modifier,
            itemColors = ListItemDefaults.colors(
                containerColor = SearchBarDefaults.colors().containerColor
            ),
            onClick = { state.eventSink(Event.OnHymnClicked(it)) }
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewSearchUi() {
    HymnalTheme { Surface { SearchScreenUi(State(persistentListOf()) {}) } }
}