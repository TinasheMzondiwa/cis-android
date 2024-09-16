package hymnal.content.impl

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hymnal.content.api.HymnalRepository
import hymnal.content.impl.utils.CoroutineRule
import hymnal.content.impl.utils.TestDispatcherProvider
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import hymnal.content.model.HymnalHymns
import hymnal.prefs.HymnalPrefs
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.HymnEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class HymnalRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    private val mockHymnsDao: HymnsDao = mockk()
    private val mockCollectionsDao: CollectionsDao = mockk()
    private val mockPrefs: HymnalPrefs = mockk()

    private lateinit var repository: HymnalRepository

    @Before
    fun setup() {
        repository = HymnalRepositoryImpl(
            context = ApplicationProvider.getApplicationContext(),
            moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build(),
            hymnsDao = mockHymnsDao,
            collectionsDao = mockCollectionsDao,
            prefs = mockPrefs,
            dispatcherProvider = TestDispatcherProvider()
        )
    }

    @Test
    fun `should query hymns for selected code`() = runTest {
        // given
        val code = "english"
        val query = "again and again"
        val entities = listOf(HymnEntity(1, code, 1, "", query, null, null, null))
        every { mockPrefs.getSelectedHymnal() }.returns(code)
        coEvery { mockHymnsDao.search(code, "%$query%") }.returns(entities)

        // when
        val results = repository.searchHymns(query).getOrNull()

        // then
        results shouldBeEqualTo listOf(
            Hymn(1, code, 1, "", query, null, null, null)
        )
    }

    @Test
    fun `should return hymnals from submodule`() {
        val hymnals = repository.getHymnals()

        hymnals.isSuccess shouldBeEqualTo true
        hymnals.getOrNull()?.isNotEmpty() shouldBeEqualTo true
    }

    @Test
    fun `should return hymns from submodule`() = runTest {
        val dbFlow = MutableSharedFlow<List<HymnEntity>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val hymnal = Hymnal("english", "Christ In Song", "English")
        val entity = HymnEntity(1, hymnal.code, 1, "Title", "content", null, null, null)

        every { mockPrefs.getSelectedHymnal() }.returns("english")
        every { mockPrefs.getSelected() }.returns(flowOf("english"))
        every { mockPrefs.setSelectedHymnal(hymnal.code) }.returns(Unit)
        every { mockHymnsDao.listAll(hymnal.code) }.returns(dbFlow)
        coEvery { mockHymnsDao.insertAll(any()) }.returns(Unit)

        val hymnsCapture = mutableListOf<List<HymnEntity>>()

        repository.getHymns().test {
            dbFlow.emit(emptyList())

            awaitItem() shouldBeEqualTo Result.success(HymnalHymns(hymnal, emptyList()))

            dbFlow.emit(listOf(entity))

            awaitItem() shouldBeEqualTo Result.success(HymnalHymns(hymnal, listOf(entity.toHymn())))

            coVerify { mockHymnsDao.insertAll(capture(hymnsCapture)) }

            // 300 english hymns
            hymnsCapture.first().size shouldBeEqualTo 300
        }
    }
}
