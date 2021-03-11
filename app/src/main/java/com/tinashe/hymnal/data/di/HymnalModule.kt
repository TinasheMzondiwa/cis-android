package com.tinashe.hymnal.data.di

import android.app.Application
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinashe.hymnal.data.model.cfg.DonationsConfig
import com.tinashe.hymnal.extensions.coroutines.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HymnalModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    fun provideFirebaseConfig(): FirebaseRemoteConfig {
        return Firebase.remoteConfig.also {
            it.fetchAndActivate()
        }
    }

    @Provides
    fun provideConfig(remoteConfig: FirebaseRemoteConfig, moshi: Moshi): DonationsConfig {
        val jsonString = remoteConfig.getString("cfg_donations")
        return if (jsonString.isEmpty()) {
            DonationsConfig()
        } else {
            val jsonAdapter: JsonAdapter<DonationsConfig> =
                moshi.adapter(DonationsConfig::class.java)
            jsonAdapter.fromJson(jsonString) ?: DonationsConfig()
        }
    }

    @Provides
    fun provideSchedulers(): SchedulerProvider = SchedulerProvider()
}
