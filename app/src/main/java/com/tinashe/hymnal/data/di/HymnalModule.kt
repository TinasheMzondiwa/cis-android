package com.tinashe.hymnal.data.di

import android.app.Application
import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinashe.hymnal.data.model.cfg.DonationsConfig
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
    fun provideReviewManager(context: Context): ReviewManager = ReviewManagerFactory.create(context)

    @Provides
    fun provideConfig(remoteConfig: FirebaseRemoteConfig, moshi: Moshi): DonationsConfig {
        val jsonString = remoteConfig.getString("cfg_donations")
        val jsonAdapter: JsonAdapter<DonationsConfig> = moshi.adapter(DonationsConfig::class.java)
        return jsonAdapter.fromJson(jsonString) ?: DonationsConfig()
    }
}
