package com.tinashe.hymnal.data.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

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
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database.also {
        try {
            it.setPersistenceEnabled(true)
        } catch (ex: DatabaseException) {
            Timber.e(ex)
        }
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideReviewManager(context: Context): ReviewManager = ReviewManagerFactory.create(context)
}
