package com.smascaro.trackmixing.trackslist.controller


import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class TracksListController @Inject constructor(
    private val mFetchDownloadedTracks: FetchDownloadedTracks,
    private val playbackSession: PlaybackSession,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<TracksListViewMvc>(navigationHelper),
    TracksListViewMvc.Listener,
    FetchDownloadedTracks.Listener {

    override fun onSearchNavigationButtonClicked() {
        navigateToSearch()
    }

    private fun navigateToSearch() {
        navigationHelper.toSearch()
    }

    private fun loadTracks() {
        GlobalScope.launch {
            mFetchDownloadedTracks.fetchTracksAndNotify(FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC)
        }
    }

    fun onStart() {
        viewMvc.registerListener(this)
        mFetchDownloadedTracks.registerListener(this)
        loadTracks()
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        mFetchDownloadedTracks.unregisterListener(this)
    }

    override fun onTrackClicked(track: Track) {
        playbackSession.startPlayback(track)
    }

    override fun onTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }
}