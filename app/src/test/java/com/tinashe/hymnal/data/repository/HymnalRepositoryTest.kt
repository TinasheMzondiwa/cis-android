package com.tinashe.hymnal.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.model.remote.RemoteHymnal
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.CoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
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
import org.mockito.Mockito.verify
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
    private lateinit var mockHymnalsDao: HymnalsDao

    @Mock
    private lateinit var mockHymnsDao: HymnsDao

    @Mock
    private lateinit var mockCollectionsDao: CollectionsDao

    @Mock
    private lateinit var mockPrefs: HymnalPrefs

    @Mock
    private lateinit var mockRemoteHymnsRepository: RemoteHymnsRepository

    private lateinit var repository: HymnalRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        repository = HymnalRepository(
            mockHymnalsDao, mockHymnsDao, mockCollectionsDao, mockPrefs, mockRemoteHymnsRepository, TestCoroutineDispatcher()
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should load sample hymnal on first launch`() = runBlockingTest {
        // given
        val code = "english"
        val title = "Christ In Song"
        val language = "English"
        val hymns = emptyList<Hymn>()
        val mockHymnal = mock<RemoteHymnal>().also {
            `when`(it.key).thenReturn(code)
            `when`(it.title).thenReturn(title)
            `when`(it.language).thenReturn(language)
            `when`(it.hymns).thenReturn(emptyList())
        }
        `when`(mockRemoteHymnsRepository.getSample()).thenReturn(mockHymnal)

        // when
        repository.getHymns().collect {
            if (it.status == Status.SUCCESS) {
                verify(mockHymnalsDao.insert(Hymnal(code, title, language)))
                verify(mockHymnsDao.insertAll(hymns))
                verify(mockPrefs.setSelectedHymnal(code))
            }
        }
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
