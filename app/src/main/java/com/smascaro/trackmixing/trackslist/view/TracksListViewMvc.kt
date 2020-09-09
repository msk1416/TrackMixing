package com.smascaro.trackmixing.trackslist.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc


interface TracksListViewMvc :
    ObservableViewMvc<TracksListViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
        fun onSearchNavigationButtonClicked()
    }

    fun bindTracks(tracks: List<Track>)
}