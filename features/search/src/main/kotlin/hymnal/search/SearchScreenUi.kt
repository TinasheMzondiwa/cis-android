package hymnal.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
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
                onQueryChange = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                onSearch = { },
                expanded = true,
                onExpandedChange = { },
                placeholder = { Text(stringResource(L10nR.string.hint_search_hymns)) },
                leadingIcon = {
                    IconButton(onClick = { state.eventSink(Event.OnNavBackClicked)}) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(L10nR.string.navigation_back)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = { textFieldState.clearText() }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                }
            )
        },
        expanded = true,
        onExpandedChange = { },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier,
        ) {
            val list = List(100) { "Text $it" }
            val matched = list.filter { it.contains(textFieldState.text, ignoreCase = true) }

            items(matched, key = { it }) { text ->
                ListItem(
                    headlineContent = { Text(text) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    colors = ListItemDefaults.colors(
                        containerColor = SearchBarDefaults.colors().containerColor
                    ),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSearchUi() {
    HymnalTheme { Surface { SearchScreenUi(State {})  } }
}