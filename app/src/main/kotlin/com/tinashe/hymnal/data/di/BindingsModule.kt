package com.tinashe.hymnal.data.di

import com.tinashe.hymnal.extensions.prefs.HymnalPrefsImpl
import com.tinashe.hymnal.ui.support.BillingManager
import com.tinashe.hymnal.ui.support.BillingManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hymnal.prefs.HymnalPrefs

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    internal abstract fun bindBillingManager(impl: BillingManagerImpl): BillingManager

    @Binds
    internal abstract fun bindHymnalPrefs(impl: HymnalPrefsImpl): HymnalPrefs
}
