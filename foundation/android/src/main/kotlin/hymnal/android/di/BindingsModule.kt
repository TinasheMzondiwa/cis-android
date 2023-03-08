
package hymnal.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hymnal.android.coroutines.DefaultDispatcherProvider
import hymnal.android.coroutines.DispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    internal abstract fun bindDispatcherProvider(
        provider: DefaultDispatcherProvider
    ): DispatcherProvider

}
