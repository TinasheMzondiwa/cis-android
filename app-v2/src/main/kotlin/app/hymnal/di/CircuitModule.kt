package app.hymnal.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CircuitModule {

    companion object {
        @Provides
        @Singleton
        fun provideCircuit(
            uiFactories: Set<@JvmSuppressWildcards Ui.Factory>,
            presenterFactories: Set<@JvmSuppressWildcards Presenter.Factory>
        ): Circuit =
            Circuit.Builder()
                .addFactories(presenterFactories, uiFactories)
                .build()

        private fun Circuit.Builder.addFactories(
            presenterFactories: Set<Presenter.Factory>,
            uiFactories: Set<Ui.Factory>,
        ) = apply {
            for (factory in presenterFactories) {
                addPresenterFactory(factory)
            }
            for (factory in uiFactories) {
                addUiFactory(factory)
            }
        }
    }

}
