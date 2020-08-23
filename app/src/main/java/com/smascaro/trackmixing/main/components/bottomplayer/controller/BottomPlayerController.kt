package com.smascaro.trackmixing.main.components.bottomplayer.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.toModel
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.components.bottomplayer.model.BottomPlayerData
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class BottomPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val tracksRepository: TracksRepository,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<BottomPlayerViewMvc>(navigationHelper),
    BottomPlayerViewMvc.Listener {

    private var currentTrack: Track? = null
    private var currentState: PlaybackStateManager.PlaybackState? = null

    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.registerListener(this)
        viewMvc.onCreate()
    }

    private fun updateCurrentPlayingTrack() =
        runBlocking {
            currentState = playbackStateManager.getPlayingState()
            if (currentState is PlaybackStateManager.PlaybackState.Playing || currentState is PlaybackStateManager.PlaybackState.Paused) {
                val songId = playbackStateManager.getCurrentSong()
                currentTrack = tracksRepository.get(songId).toModel()
                when (currentState) {
                    is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
                    is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
                }
                viewMvc.showPlayerBar(
                    makeBottomPlayerData()
                )
            } else if (currentState is PlaybackStateManager.PlaybackState.Stopped) {
                viewMvc.hidePlayerBar()
            }
        }

    private fun makeBottomPlayerData(): BottomPlayerData {
        return BottomPlayerData(
            currentTrack!!.title,
            currentState!!,
            currentTrack!!.thumbnailUrl
        )
    }

    fun onDestroy() {
        viewMvc.unregisterListener(this)
    }

    override fun onLayoutClick() {
        navigateToPlayer()
    }

    fun navigateToPlayer() {
        navigationHelper.toPlayer(currentTrack!!)
    }

    override fun onActionButtonClicked() {
        val currentState = playbackStateManager.getPlayingState()
        if (currentState is PlaybackStateManager.PlaybackState.Playing) {
            EventBus.getDefault().post(PlaybackEvent.PauseMasterEvent())
            Timber.d("Sent a PauseMasterEvent")
        } else if (currentState is PlaybackStateManager.PlaybackState.Paused) {
            EventBus.getDefault().post(PlaybackEvent.PlayMasterEvent())
            Timber.d("Sent a PlayMasterEvent")
        }
    }

    override fun onPlayerStateChanged() {
        updateCurrentPlayingTrack()
    }

    override fun onServiceRunningCheck(running: Boolean) {
        if (running) {
            updateCurrentPlayingTrack()
        }
    }
}