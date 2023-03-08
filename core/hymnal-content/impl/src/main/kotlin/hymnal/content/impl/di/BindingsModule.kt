package hymnal.content.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hymnal.content.api.HymnalRepository
import hymnal.content.impl.HymnalRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BindingsModule {

    @Binds
    internal abstract fun bindHymnalRepository(
        impl: HymnalRepositoryImpl
    ): HymnalRepository
}