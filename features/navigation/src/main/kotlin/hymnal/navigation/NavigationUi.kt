package hymnal.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import libraries.hymnal.navigation.api.NavigationScreen
import libraries.hymnal.navigation.model.NavItem
import libraries.hymnal.ui.HymnalTheme
import hymnal.l10n.R as L10nR

private data class NavItemModel(
    @StringRes val title: Int,
    val icon: ImageVector,
    val item: NavItem,
)

@CircuitInject(NavigationScreen::class, SingletonComponent::class)
@Composable
fun NavigationUi(state: State, modifier: Modifier = Modifier) {
    val models by remember { mutableStateOf(NavItem.entries.map { it.toModel() }) }

    NavigationBar(modifier = modifier) {
        models.forEach { model ->
            NavigationBarItem(
                selected = state.selected == model.item,
                onClick = { state.eventSink(Event.OnItemSelected(model.item)) },
                icon = { Icon(model.icon, contentDescription = stringResource(model.title)) },
                label = { Text(text = stringResource(model.title)) }
            )
        }
    }
}

private fun NavItem.toModel(): NavItemModel {
    val (title, icon) = when (this) {
        NavItem.HYMNS -> L10nR.string.title_hymns to Icons.AutoMirrored.Rounded.QueueMusic
        NavItem.COLLECTIONS -> L10nR.string.title_collections to Icons.AutoMirrored.Rounded.LibraryBooks
        NavItem.SUPPORT -> L10nR.string.title_support to Icons.Rounded.Support
        NavItem.INFO -> L10nR.string.title_info to Icons.Rounded.Info
    }

    return NavItemModel(
        title = title,
        icon = icon,
        item = this,
    )
}

@PreviewLightDark
@Composable
private fun PreviewNavigationBar() {
    var selected by remember { mutableStateOf(NavItem.HYMNS) }
    HymnalTheme {
        Surface {
            NavigationUi(State(selected = selected) { event ->
                when (event) {
                    is Event.OnItemSelected -> selected = event.item
                }
            })
        }
    }
}
