package hymnal.content.impl

import android.content.Context
import com.squareup.moshi.Moshi
import hymnal.content.api.HymnalRepository
import hymnal.content.impl.utils.CoroutineRule
import hymnal.content.impl.utils.TestDispatcherProvider
import hymnal.content.model.Hymn
import hymnal.prefs.HymnalPrefs
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.HymnEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class HymnalRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    private val mockContext: Context = mockk()
    private val mockMoshi: Moshi = mockk()
    private val mockHymnsDao: HymnsDao = mockk()
    private val mockCollectionsDao: CollectionsDao = mockk()
    private val mockPrefs: HymnalPrefs = mockk()

    private lateinit var repository: HymnalRepository

    @Before
    fun setup() {
        repository = HymnalRepositoryImpl(
            mockContext,
            mockMoshi,
            mockHymnsDao,
            mockCollectionsDao,
            mockPrefs,
            TestDispatcherProvider()
        )
    }

    @Test
    fun `should query hymns for selected code`() = runTest {
        // given
        val code = "english"
        val query = "again and again"
        val entities = listOf(HymnEntity(1, code, 1, "", query, null, null))
        every { mockPrefs.getSelectedHymnal() }.returns(code)
        coEvery { mockHymnsDao.search(code, "%$query%") }.returns(entities)

        // when
        val results = repository.searchHymns(query).getOrNull()

        // then
        results shouldBeEqualTo listOf(
            Hymn(1, code, 1, "", query, null, null)
        )
    }
}
