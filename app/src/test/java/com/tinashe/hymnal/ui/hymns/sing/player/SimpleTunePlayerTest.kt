package com.tinashe.hymnal.ui.hymns.sing.player

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.utils.observeAll
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
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockAssets: AssetManager

    @Mock
    private lateinit var mockPrefs: HymnalPrefs

    private lateinit var tunePlayer: SimpleTunePlayer

    @Before
    fun setup() {
        `when`(mockContext.assets).thenReturn(mockAssets)

        tunePlayer = SimpleTunePlayer(mockContext, mockPrefs)
    }

    @Test
    fun `should return true when midi file is available and language code supports media`() {
        // given
        val assetFile = "midis/5.mid"
        `when`(mockAssets.openFd(assetFile)).thenReturn(mock())
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo true
    }

    @Test
    fun `should not crash but return false when midi file is un-available`() {
        // given
        val assetFile = "midis/5.mid"
        `when`(mockAssets.openFd(assetFile)).thenAnswer { throw IOException() }
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo false
    }

    @Test
    fun `should return false when language code does not support media`() {
        // given
        val assetFile = "midis/5.mid"
        `when`(mockAssets.openFd(assetFile)).thenReturn(mock())
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("swahili")

        // when
        val canPlay = tunePlayer.canPlayTune(5)

        // then
        canPlay shouldBeEqualTo false
    }

    @Test
    fun `should emit states ON_PLAY then ON_STOP for an unsuccessful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
    fun `should emit states ON_PLAY then ON_COMPLETE for a successful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
    fun `should emit states ON_PLAY then ON_STOP for a successful playback`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
    fun `should pause media on lifecyle pause`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
    fun `should pause media on toggle`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
    fun `should pause media when new hymn is in view`() {
        // given
        val assetFile = "midis/5.mid"
        val mockFileDescriptor = mock<AssetFileDescriptor>().also {
            `when`(it.fileDescriptor).thenReturn(mock())
            `when`(it.startOffset).thenReturn(0L)
            `when`(it.length).thenReturn(0L)
        }
        val source = DataSource.toDataSource(
            mockFileDescriptor.fileDescriptor,
            mockFileDescriptor.startOffset,
            mockFileDescriptor.length
        )
        ShadowMediaPlayer.addMediaInfo(source, ShadowMediaPlayer.MediaInfo())
        `when`(mockAssets.openFd(assetFile)).thenReturn(mockFileDescriptor)
        `when`(mockPrefs.getSelectedHymnal()).thenReturn("english")
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
