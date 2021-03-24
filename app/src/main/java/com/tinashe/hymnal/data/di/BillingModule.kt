package com.tinashe.hymnal.data.di

import com.tinashe.hymnal.extensions.coroutines.SchedulerProvider
import com.tinashe.hymnal.ui.support.BillingManager
import com.tinashe.hymnal.ui.support.BillingManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingManager(schedulerProvider: SchedulerProvider): BillingManager =
        BillingManagerImpl(schedulerProvider)
}
