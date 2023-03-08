package hymnal.storage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hymnal.storage.HymnalDatabase
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnalsDao
import hymnal.storage.dao.HymnsDao

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    fun provideHymnalsDao(
        @ApplicationContext context: Context
    ): HymnalsDao = context.database().hymnalsDao()

    @Provides
    fun provideHymnsDao(
        @ApplicationContext context: Context
    ): HymnsDao = context.database().hymnsDao()

    @Provides
    fun provideCollectionsDao(
        @ApplicationContext context: Context
    ): CollectionsDao = context.database().collectionsDao()
}

private fun Context.database(): HymnalDatabase = HymnalDatabase.getInstance(this)