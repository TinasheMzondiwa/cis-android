package app.hymnal.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Support
import androidx.compose.ui.graphics.vector.ImageVector
import libraries.hymnal.navigation.model.NavItem
import hymnal.l10n.R as L10nR

data class NavItemModel(
    @StringRes val title: Int,
    val icon: ImageVector,
    val item: NavItem,
)

fun NavItem.toModel(): NavItemModel {
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