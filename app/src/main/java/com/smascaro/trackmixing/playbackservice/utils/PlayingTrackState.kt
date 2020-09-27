package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.MEDIA_PLAYER_MAX_VOLUME
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import kotlin.math.ln

class PlayingTrackState(
    val instrument: TrackInstrument,
    private val context: Context
) : BaseObservable<PlayingTrackState.Listener>(), Player.EventListener {
    interface Listener {
        fun onPlayerPrepared(instrument: TrackInstrument)
        fun onPlayerCompletion(instrument: TrackInstrument)
        fun onPlayerError(instrument: TrackInstrument, errorMessage: String)
    }

    companion object {
        private val VOCALS_FILENAME = "vocals.mp3"
        private val OTHER_FILENAME = "other.mp3"
        private val BASS_FILENAME = "bass.mp3"
        private val DRUMS_FILENAME = "drums.mp3"
        fun create(track: Track, instrument: TrackInstrument, context: Context): PlayingTrackState {
            val filename = when (instrument) {
                TrackInstrument.VOCALS -> VOCALS_FILENAME
                TrackInstrument.OTHER -> OTHER_FILENAME
                TrackInstrument.BASS -> BASS_FILENAME
                TrackInstrument.DRUMS -> DRUMS_FILENAME
            }
            val playingTrackState = PlayingTrackState(
                instrument,
                context
            ).apply {
                initialize("${track.downloadPath}/$filename")
            }
            return playingTrackState
        }
    }

    enum class LogicMediaState {
        MUTED, PLAYING
    }

    val maxVolume: Float = MEDIA_PLAYER_MAX_VOLUME
    private var mIsPrepared: Boolean = false
    private lateinit var player: ExoPlayer
    private var mVolume = maxVolume
    private var hasTrackCompletedPlaying: Boolean = false
    private lateinit var progressiveMediaSourceFactory: ProgressiveMediaSource.Factory
    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    var readyToPlay: Boolean = mIsPrepared
        get() = mIsPrepared
        private set

    fun initialize(path: String) {
        hasTrackCompletedPlaying = false
        val renderersFactory = DefaultRenderersFactory(context)
        player = SimpleExoPlayer.Builder(context, renderersFactory).build()
        player.playWhenReady = true
        progressiveMediaSourceFactory =
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context))
        val mediaSource = progressiveMediaSourceFactory.createMediaSource(MediaItem.fromUri(path))
        player.setMediaSource(mediaSource)
        player.prepare()
        player.addListener(this)
    }


    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            SimpleExoPlayer.STATE_BUFFERING,
            SimpleExoPlayer.STATE_READY,
            SimpleExoPlayer.STATE_IDLE -> {
            }
            SimpleExoPlayer.STATE_ENDED -> handleTrackCompletion()
        }
    }

    private fun handleTrackCompletion() {
        hasTrackCompletedPlaying = true
        getListeners().forEach {
            it.onPlayerCompletion(instrument)
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        if (!isLoading) {
            mIsPrepared = true
            getListeners().forEach {
                it.onPlayerPrepared(instrument)
            }
        } else {
            mIsPrepared = false
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        getListeners().forEach { it.onPlayerError(instrument, error.localizedMessage) }
    }

    fun setVolume(volume: Int) {
        mVolume = volume.toFloat()
        updateVolume()
    }

    fun play() {
        player.playbackLooper.thread.run {
            player.play()
        }
    }

    fun pause() {
        player.playbackLooper.thread.run {
            player.pause()
        }
    }

    fun finalize() {
        mIsPrepared = false
        player.stop()
        player.release()
        player.removeListener(this)
    }

    fun isCompleted(): Boolean {
        return hasTrackCompletedPlaying
    }

    private fun updateVolume() {
        val mediaPlayerVolume = (1 - (ln(maxVolume - mVolume) / ln(maxVolume)))
        player.audioComponent?.volume = mediaPlayerVolume
    }

    fun getVolume(): Float {
        return mVolume
    }

    fun getTimestampMillis(): Long {
        return player.currentPosition
    }

    fun seek(timestampSeconds: Long) {
        player.seekTo(timestampSeconds * 1000)
    }
}