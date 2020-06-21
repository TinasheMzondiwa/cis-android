package com.tinashe.hymnal.data.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
internal class RemoteHymnsRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockStorage: FirebaseStorage

    @Mock
    private lateinit var mockMoshi: Moshi

    @Mock
    private lateinit var mockContext: Context

    private lateinit var repository: RemoteHymnsRepository

    @Before
    fun setup() {
        repository = RemoteHymnsRepository(
            mockDatabase, mockAuth, mockStorage, mockMoshi, mockContext
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should be signed in before interacting with database`() = runBlockingTest {
        // when
        repository.listHymnals()

        // then
        verify(mockAuth).signInAnonymously()
        verifyNoInteractions(mockDatabase)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should be signed in before interacting with storage`() = runBlockingTest {
        // when
        repository.downloadHymns("code")

        // then
        verify(mockAuth).signInAnonymously()
        verifyNoInteractions(mockStorage)
    }
}
