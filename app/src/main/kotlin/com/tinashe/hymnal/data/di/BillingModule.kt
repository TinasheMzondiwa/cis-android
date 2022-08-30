package com.tinashe.hymnal.data.di

import com.tinashe.hymnal.ui.support.BillingManager
import com.tinashe.hymnal.ui.support.BillingManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    internal abstract fun bindBillingManager(impl: BillingManagerImpl): BillingManager
}
