package libraries.hymnal.hymns.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import libraries.hymnal.hymns.ui.model.HymnModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HymnsList(
    hymns: ImmutableList<HymnModel>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    itemColors: ListItemColors = ListItemDefaults.colors(),
    onClick: (HymnModel) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        state = state,
    ) {
        items(hymns, key = { it.id }) { hymn ->
            ListItem(
                headlineContent = { Text(hymn.title) },
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick(hymn) },
                colors = itemColors,
            )
        }
    }
}