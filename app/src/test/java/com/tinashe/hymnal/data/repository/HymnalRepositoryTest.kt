package com.tinashe.hymnal.data.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.CoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
internal class HymnalRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockMoshi: Moshi

    @Mock
    private lateinit var mockHymnsDao: HymnsDao

    @Mock
    private lateinit var mockCollectionsDao: CollectionsDao

    @Mock
    private lateinit var mockPrefs: HymnalPrefs

    private lateinit var repository: HymnalRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        repository = HymnalRepository(
            mockContext,
            mockMoshi,
            mockHymnsDao,
            mockCollectionsDao,
            mockPrefs,
            TestCoroutineDispatcher()
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should query hymns for selected code`() = runBlockingTest {
        // given
        val code = "english"
        val query = "again and again"
        val mockResults = listOf<Hymn>(mock())
        `when`(mockPrefs.getSelectedHymnal()).thenReturn(code)
        `when`(mockHymnsDao.search(code, "%$query%")).thenReturn(mockResults)

        // when
        val results = repository.searchHymns(query)

        // then
        results shouldBeEqualTo mockResults
    }
}
