package app.hymnal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.hymnal.model.NavItemModel
import app.hymnal.model.toModel
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import kotlinx.collections.immutable.toImmutableList
import libraries.hymnal.navigation.api.CollectionsScreen
import libraries.hymnal.navigation.api.HymnsScreen
import libraries.hymnal.navigation.api.InfoScreen
import libraries.hymnal.navigation.api.SupportScreen
import libraries.hymnal.navigation.model.NavItem

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
) : Presenter<HomeState> {

    @CircuitInject(HomeScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HomePresenter
    }

    @Composable
    override fun present(): HomeState {
        var currentNavItem by rememberRetained { mutableStateOf(NavItem.HYMNS.toModel()) }
        val currentScreen by rememberRetained(currentNavItem) {
            mutableStateOf(currentNavItem.toScreen())
        }
        val navEntries by rememberRetained {
            mutableStateOf(NavItem.entries.map { it.toModel() }.toImmutableList())
        }

        return HomeState(
            currentNavItem = currentNavItem,
            currentScreen = currentScreen,
            navEntries = navEntries,
        ) { event ->
            when (event) {
                is Event.OnItemSelected -> {
                    currentNavItem = event.item
                }

                is Event.OnNavEvent -> {
                    // Handle navigation events
                }
            }
        }
    }

}

private fun NavItemModel.toScreen(): Screen {
    return when (item) {
        NavItem.HYMNS -> HymnsScreen
        NavItem.COLLECTIONS -> CollectionsScreen
        NavItem.SUPPORT -> SupportScreen
        NavItem.INFO -> InfoScreen
    }
}