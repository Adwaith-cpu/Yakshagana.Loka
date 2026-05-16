package com.yakshaganaloka.app.data.audio

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.yakshaganaloka.app.models.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentAudio = MutableStateFlow<Audio?>(null)
    val currentAudio = _currentAudio.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(state: Int) {
                    _playbackState.value = state
                }
            })
        }, MoreExecutors.directExecutor())
    }

    fun play(audio: Audio) {
        val player = controller ?: return
        _currentAudio.value = audio
        
        // Add Metadata for System Notification (Title, Artist/Category)
        val metadata = MediaMetadata.Builder()
            .setTitle(audio.title)
            .setArtist(audio.category)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(audio.audioId)
            .setUri(audio.audioUrl)
            .setMediaMetadata(metadata)
            .build()
            
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlay() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun stop() {
        controller?.stop()
        _currentAudio.value = null
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
