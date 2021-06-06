package com.tinashe.hymnal.data.repository

import android.content.Context
import com.squareup.moshi.Moshi
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.CoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class HymnalRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    private val mockContext: Context = mockk()
    private val mockMoshi: Moshi = mockk()
    private val mockHymnsDao: HymnsDao = mockk()
    private val mockCollectionsDao: CollectionsDao = mockk()
    private val mockPrefs: HymnalPrefs = mockk()

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
        val mockResults = listOf<Hymn>(mockk())
        every { mockPrefs.getSelectedHymnal() }.returns(code)
        coEvery { mockHymnsDao.search(code, "%$query%") }.returns(mockResults)

        // when
        val results = repository.searchHymns(query)

        // then
        results shouldBeEqualTo mockResults
    }
}
