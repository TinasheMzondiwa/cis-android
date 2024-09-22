package hymnal.hymns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import hymnal.hymns.model.HymnModel
import kotlinx.collections.immutable.persistentListOf
import libraries.hymnal.navigation.api.HymnsScreen
import libraries.hymnal.ui.HymnalTheme
import hymnal.l10n.R as L10nR

@CircuitInject(HymnsScreen::class, SingletonComponent::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsUi(state: State, modifier: Modifier = Modifier) {
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            text = (state as? State.Content)?.title
                                ?: stringResource(L10nR.string.app_name)
                        )

                        AnimatedVisibility(scrollBehavior.state.collapsedFraction == 0f) {
                            Spacer(
                                Modifier
                                    .padding(top = 16.dp)
                                    .size(48.dp, 4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                },
                modifier = Modifier,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = stringResource(L10nR.string.title_search),
                        )

                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Rounded.Dialpad,
                            contentDescription = stringResource(L10nR.string.title_number),
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.LibraryBooks,
                            contentDescription = stringResource(L10nR.string.title_number),
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when (state) {
            is State.Content -> LazyColumn(
                modifier = Modifier.padding(paddingValues),
                state = listState,
            ) {
                items(state.hymns, key = { it.id}) { hymn ->
                    ListItem(
                        headlineContent = { Text(hymn.title) },
                        modifier = Modifier.animateItem()
                    )
                }
            }

            State.Loading -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        HymnsUi(
            State.Content(
                "Title", persistentListOf(
                    HymnModel(1, "Title 1", 1),
                    HymnModel(2, "Title 2", 2),
                    HymnModel(3, "Title 3", 3),
                    HymnModel(4, "Title 4", 4),
                    HymnModel(5, "Title 5", 5),
                )
            )
        )
    }
}