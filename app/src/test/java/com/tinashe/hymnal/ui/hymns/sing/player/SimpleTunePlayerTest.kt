package com.tinashe.hymnal.ui.hymns.sing.player

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.observeAll
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowMediaPlayer::class])
internal class SimpleTunePlayerTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val mockContext: Context = mockk()
    private val mockAssets: AssetManager = mockk()
    private val mockPrefs: HymnalPrefs = mockk()

    private lateinit var tunePlayer: SimpleTunePlayer

    @Before
    fun setup() {
        every { mockContext.assets }.returns(mockAssets)

        tunePlayer = SimpleTunePlayer(mockContext, mockPrefs)
    }

    @Test
    fun `should return true when midi file is available and language code supports media`() {
        // given
        val assetFile = "midis/5.mid"
        every { mockAssets.openFd(assetFile) }.returns(mockk())
        every { mockPrefs.getSelectedHymnal() }.returns("english")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo true
    }

    @Test
    fun `should not crash but return false when midi file is un-available`() {
        // given
        val assetFile = "midis/5.mid"
        every { mockAssets.openFd(assetFile) }.answers { throw IOException() }
        every { mockPrefs.getSelectedHymnal() }.returns("english")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo false
    }

    @Test
    fun `should return false when language code does not support media`() {
        // given
        val assetFile = "midis/5.mid"
        every { mockAssets.openFd(assetFile) }.returns(mockk())
        every { mockPrefs.getSelectedHymnal() }.returns("swahili")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo false
    }

    @Test
    fun `should emit states ON_PLAY then ON_STOP for an unsuccessful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        // ShadowDataSource not set-up
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_STOP
        )
    }

    @Test
    @Ignore("Fix test")
    fun `should emit states ON_PLAY then ON_COMPLETE for a successful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)
        val shadowPlayer = shadowOf(tunePlayer.mediaPlayer)
        shadowPlayer.onCompletionListener.onCompletion(tunePlayer.mediaPlayer)

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_PLAY,
            PlaybackState.ON_COMPLETE
        )
    }

    @Test
    @Ignore("Fix test")
    fun `should emit states ON_PLAY then ON_STOP for a successful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)
        tunePlayer.stopMedia()

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_PLAY,
            PlaybackState.ON_STOP
        )
    }

    @Test
    @Ignore("Fix test")
    fun `should pause media on lifecyle pause`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)
        tunePlayer.onPause()

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_PLAY,
            PlaybackState.ON_STOP
        )
    }

    @Test
    @Ignore("Fix test")
    fun `should pause media on toggle`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)
        tunePlayer.togglePlayTune(5)

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_PLAY,
            PlaybackState.ON_STOP
        )
    }

    @Test
    @Ignore("Fix test")
    fun `should pause media when new hymn is in view`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mockk<AssetFileDescriptor>().also {
            every { it.fileDescriptor }.returns(mockk())
            every { it.startOffset }.returns(0L)
            every { it.length }.returns(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        every { mockAssets.openFd(assetFile) }.returns(mockFileDescriptor)
        every { mockAssets.openFd("midis/6.mid") }.returns(mockFileDescriptor)
        every { mockPrefs.getSelectedHymnal() }.returns("english")
        val states = tunePlayer.playbackLiveData.observeAll()

        // when
        tunePlayer.togglePlayTune(5)
        tunePlayer.canPlayTune(6)

        // then
        states shouldBeEqualTo listOf(
            PlaybackState.ON_PLAY,
            PlaybackState.ON_STOP
        )
    }
}
