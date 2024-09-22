package app.hymnal

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent

class HomePresenter @AssistedInject constructor() : Presenter<HomeState> {

    @CircuitInject(HomeScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory  {
        fun create(): HomePresenter
    }

    @Composable
    override fun present(): HomeState = HomeState
}