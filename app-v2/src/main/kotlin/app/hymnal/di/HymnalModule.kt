package app.hymnal.di

import android.content.Context
import app.hymnal.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hymnal.android.coroutines.DispatcherProvider
import hymnal.prefs.HymnalPrefs
import libraries.hymnal.models.AppConfig
import services.hymnal.prefs.impl.HymnalPrefsImpl

@Module
@InstallIn(SingletonComponent::class)
object HymnalModule {

    @Provides
    fun provideAppConfig(): AppConfig {
        return AppConfig(
            packageName = BuildConfig.APPLICATION_ID,
            version = BuildConfig.VERSION_NAME,
            isDebug = BuildConfig.DEBUG
        )
    }

    @Provides
    fun providePrefs(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider,
    ): HymnalPrefs = HymnalPrefsImpl(
        context = context,
        dataStore = context.dataStore,
        dispatcherProvider = dispatcherProvider,
    )
}
